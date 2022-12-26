package rs.biljnaapotekasvstefan.ordertrack.client;


import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import rs.biljnaapotekasvstefan.ordertrack.controller.SendEmail;
import rs.biljnaapotekasvstefan.ordertrack.model.Orders;
import rs.biljnaapotekasvstefan.ordertrack.model.OrdersStatusId;
import rs.biljnaapotekasvstefan.ordertrack.model.OrdersStatuses;
import rs.biljnaapotekasvstefan.ordertrack.repository.OrdersRepository;
import rs.biljnaapotekasvstefan.ordertrack.repository.OrdersStatusesRepository;
import rs.biljnaapotekasvstefan.ordertrack.scrape.engine.ScrapeLoader;
import rs.biljnaapotekasvstefan.ordertrack.scrape.helper.ScrapeHelper;
import rs.biljnaapotekasvstefan.ordertrack.scrape.loader.PageLoaderImpl;

import javax.mail.internet.AddressException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class OrderStatus {

    @Value("${url.dexpress}")
    private String dexpressUrl;

    @Autowired
    ScrapeLoader scrapeLoader;

    @Autowired
    PageLoaderImpl pageLoader;

    @Autowired
    OrdersStatusesRepository ordersStatusesRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    SendEmail sendEmail;

    //At minute 45 past hour 10 and 14 on every day-of-week from Monday through Friday.
    @Scheduled(cron = "0 30 9,11,14,16,18 * * 1-5")
    @GetMapping(value = "/check")
    public String CheckOrders() throws MalformedURLException, InterruptedException {
        System.out.println(LocalDateTime.now());

        //List<Orders> ordersList = ordersRepository.findOrderByStatusNot(1);
        List<Orders> ordersList = ordersRepository.findOrderByStatusNotAndCustomersUsersUsername(1, "line");
        ordersList.forEach(orders -> LoadPageAndCheckOrderStatus(orders));

        return "redirect:/";
    }

    public void LoadPageAndCheckOrderStatus(Orders order) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_PURPLE = "\u001B[35m";
        String content = null;
        Elements orderElements = null;
        OrdersStatuses ordersStatuses = new OrdersStatuses();
        OrdersStatusId ordersStatusId= new OrdersStatusId();
        ordersStatusId.setOrderId(order.getOrderId());
        try {

            content = scrapeLoader.loadAndGetPageContent(new URL(dexpressUrl + order.getOrderId()), pageLoader);

            orderElements = ScrapeHelper.getElements(ScrapeHelper.createDocument(content), "div.form-tracking-info table tbody tr td");

            ordersStatusId.setCurrentStatus(orderElements.get(11).text());

            ordersStatuses.setLocation(orderElements.get(13).text());
            ordersStatuses.setStatusTime(LocalDateTime.parse(orderElements.get(9).text(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            ordersStatuses.setRegionalCenterPhone(orderElements.get(15).text());
            ordersStatuses.setOrders(order);
            if (orderElements.get(11).text().equals("Pošiljka je isporučena primaocu") || orderElements.get(11).text().equals("Pošiljka je vraćena pošiljaocu")) {
                System.out.println(ANSI_RED + orderElements.get(11) + " " + order.getOrderId() + ANSI_RESET);
                order.setStatus(1);
            } else {
                System.out.println(ANSI_GREEN + orderElements.get(11) + " " + order.getOrderId() + ANSI_RESET);
            }

        } catch (Exception e) {
            ordersStatusId.setCurrentStatus("Greška prilikom učitavanja stranice");
            order.setStatus(2);
            // System.out.println(ANSI_PURPLE + orderElements.eachText() + " " + order.getId() + ANSI_RESET);
            //sendEmail.sendEmails("Porudžbinu " + order.getId() + " nije moguće očitati sa D-express sajta");
            //e.printStackTrace();
        } finally {
            ordersStatuses.setOrdersStatusId(ordersStatusId);
            ordersStatusesRepository.save(ordersStatuses);
            ordersRepository.save(order);
        }
    }
    @GetMapping(value = "/")
    private String ShowOrders(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //authentication.getName();
        //List<OrdersStatuses> os = ordersStatusesRepository.findUndeliveredOrderStatusForActiveUser(authentication.getName());
        //os.stream().forEach(o-> System.out.println(o.getOrdersStatusId().getOrderId() + ' ' + o.getLocation()));

        //model.addAttribute("orders", ordersRepository.findOrderByStatusNot(1));
        model.addAttribute("ordersStatuses", ordersStatusesRepository.findUndeliveredOrderStatusForActiveUser(authentication.getName()));
        return "index";
    }

    @Scheduled(cron = "0 45 10,14,18 * * 1-5")
    @GetMapping(value = "/sendemail")
    private void SendEmailForOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StringBuilder emailBody = new StringBuilder();
        List<OrdersStatuses> ordersFailedList = ordersStatusesRepository.findUndeliveredOrderStatusForActiveUser(authentication.getName());
        //ordersFailedList.stream().filter(e -> e.getCurrentStatus().equals("Pošiljka je preuzeta od pošiljaoca")).filter(ae -> ae.getLocation().contains("U magacinu")).collect(Collectors.toList());
        for (OrdersStatuses order : ordersFailedList) {
// switch

            if (order.getOrdersStatusId().getCurrentStatus().equals("Pošiljka je odbijena od strane primaoca")) {
                emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                        .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                        .append("\n\n");
            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Pokušana isporuka, nema nikoga na adresi")) {
                emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                        .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                        .append("\n\n");
            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Paket ostavljen u paketomatu")) {

                if (Duration.between(order.getStatusTime(), LocalDateTime.now()).toHours() > 24) {
                    emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                            .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                            .append("\n\n");
                }
            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Preusmerena na paketomat")) {

                if (Duration.between(order.getStatusTime(), LocalDateTime.now()).toHours() > 24) {
                    emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                            .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                            .append("\n\n");
                }
                //
            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Pošiljka se vraća pošiljaocu")) {

                emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                        .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                        .append("\n\n");
            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Pokušana isporuka, netačna je adresa primaoca")) {

                emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                        .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                        .append("\n\n");

            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Pošiljka ostavljena u paket šopu")) {

                if (Duration.between(order.getStatusTime(), LocalDateTime.now()).toHours() > 24) {
                    //System.out.println(Duration.between(order.getStatusTime(), LocalDateTime.now()).toHours());
                    emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                            .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                            .append("\n\n");
                }

            }else if (order.getOrdersStatusId().getCurrentStatus().equals("Greška prilikom učitavanja stranice")) {

                    emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                            .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                            .append("\n\n");

            } else if (order.getLocation().contains("U magacinu centra")) {

                if (Duration.between(order.getOrders().getOrderSent(), LocalDateTime.now()).toHours() > 24) {
                    emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getOrdersStatusId().getCurrentStatus()).append(" ")
                            .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                            .append("\n\n");
                }

            }

        }

            if (emailBody.length() > 0) {
                sendEmail.sendEmails(emailBody.toString());
                System.out.println(emailBody.toString());
            }
    }

}
