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
import rs.biljnaapotekasvstefan.ordertrack.model.*;
import rs.biljnaapotekasvstefan.ordertrack.repository.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    StatusesRepository statusesRepository;
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    SendEmail sendEmail;

    //At minute 45 past hour 10 and 14 on every day-of-week from Monday through Friday.
    @Scheduled(cron = "0 30 9,11,14,16,18 * * MON-SAT")
    @GetMapping(value = "/check")
    public String CheckOrders() {
        System.out.println(LocalDateTime.now());

        //List<Orders> ordersList = ordersRepository.findOrderByStatusNot(1);
        //List<Orders> ordersList = ordersRepository.findOrderByStatusNotAndCustomersUsersUsername(1, "line");
        List<Orders> ordersList = ordersRepository.findUndeliveredOrders();
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

        try {

            content = scrapeLoader.loadAndGetPageContent(new URL(dexpressUrl + order.getOrderId()), pageLoader);

            orderElements = ScrapeHelper.getElements(ScrapeHelper.createDocument(content), "div.form-tracking-info table tbody tr td");
            // setujemo status
            Statuses statuses = statusesRepository.findByStatus(orderElements.get(11).text());
            ordersStatusId.setStatusId(statuses.getStatusId());
            ordersStatusId.setOrderId(order.getOrderId());
            //ordersStatuses.getOrdersStatusId().setStatusId(statuses.getStatusId());
            //ordersStatuses.getOrdersStatusId().setOrderId(order.getOrderId());
            ordersStatuses.setStatuses(statuses);

            ordersStatuses.setLocation(orderElements.get(13).text());
            ordersStatuses.setStatusTime(LocalDateTime.parse(orderElements.get(9).text(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            ordersStatuses.setRegionalCenterPhone(orderElements.get(15).text());
            ordersStatuses.setOrdersStatusId(ordersStatusId);

            order.getOrdersStatuses().add(ordersStatuses);
            ordersRepository.save(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping(value = "/")
    private String ShowOrders(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("ordersStatuses", ordersStatusesRepository.findUndeliveredOrdersForUser(authentication.getName()));
        return "index";
    }

    @Scheduled(cron = "0 45 10,14,18 * * MON-FRI")
    @GetMapping(value = "/sendemail")
    private void SendEmailForOrders(){

        System.out.println(LocalDateTime.now());

        StringBuilder emailBody = new StringBuilder();

        List<Users> usersList = (List<Users>) usersRepository.findAll();
        //List<Emails> emailsList = (List<Emails>) emailsRepository.findAll();
        String[] sendTo = null;
        //String[] s = emails.stream().map(e -> e.getEmail()).toArray(String[]::new);
        for (Users user:usersList) {
            List<OrdersStatuses> ordersFailedList = ordersStatusesRepository.findUndeliveredOrdersForUser(user.getUsername());

            sendTo = user.getEmails().stream().filter(e -> e.getUsers().getUsername().equals(user.getUsername())).map(u -> u.getEmail()).toArray(String[]::new);

            //List<Statuses> statusesList = (List<Statuses>) statusesRepository.findAll();

            for (OrdersStatuses order : ordersFailedList) {
               if(order.getStatuses().getTrack().equals(true) && Duration.between(order.getStatusTime(), LocalDateTime.now()).toHours() > order.getStatuses().getTimeDelay()){
                   emailBody.append(order.getOrders().getShipmentNumber()).append(" ").append(order.getOrdersStatusId().getOrderId()).append(" ").append(order.getStatuses().getStatus()).append(" ")
                           .append(order.getOrders().getCustomers().getPhone()).append(" ").append(order.getOrders().getCustomers().getName())
                           .append("\n\n");
               }
            }
            //System.out.println(sendTo);
        }

        //ordersFailedList.stream().filter(e -> e.getCurrentStatus().equals("Pošiljka je preuzeta od pošiljaoca")).filter(ae -> ae.getLocation().contains("U magacinu")).collect(Collectors.toList());
        /*for (OrdersStatuses order : ordersFailedList) {
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
*/
            if (emailBody.length() > 0) {
                sendEmail.sendEmails(emailBody.toString(), sendTo);
                //System.out.println(emailBody.toString());
            }
    }

}
