package rs.biljnaapotekasvstefan.ordertrack.client;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.biljnaapotekasvstefan.ordertrack.model.Customers;
import rs.biljnaapotekasvstefan.ordertrack.model.Orders;
import rs.biljnaapotekasvstefan.ordertrack.model.Users;
import rs.biljnaapotekasvstefan.ordertrack.repository.CustomerRepository;
import rs.biljnaapotekasvstefan.ordertrack.repository.OrdersRepository;
import rs.biljnaapotekasvstefan.ordertrack.repository.UsersRepository;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.*;


@Controller
public class ReadExcel {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    OrdersRepository ordersRepository;


    //@Scheduled(cron = "10 * * * * *")
    @PostMapping(value = "/excel/upload")
    public String readExcel(@RequestPart("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Users loggedinUser = (Users) authentication.getPrincipal();
        //String currentPrincipalName = authentication.getName();

        System.out.println(authentication.getName());
        try {
           // System.out.println(file.getOriginalFilename());
           FileInputStream files = (FileInputStream) file.getInputStream();

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(files);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            //@TODO proveriti da li ima potrebe za odlaskom u bazu ili može kroz kreiranje novog objekta tipa users
            Users login = usersRepository.findByUsername(authentication.getName());

            while (rowIterator.hasNext()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm.dd.yyyy");
                Row row = rowIterator.next();
                if(!row.getCell(0).getStringCellValue().equals("Kod pošiljke")) {
                    if (!row.getCell(0).getStringCellValue().equals("")) {

                        //Customer customer = new Customer();
                        Customers customer = customerRepository.findCustomerByPhone(row.getCell(5).getStringCellValue().replace("/", "").replace("-", ""));
                        //if(customer)
                        if(customer == null){
                            customer = new Customers();
                        }
                        //List<Orders> orders = new ArrayList<>();

                        customer.setName(row.getCell(1).getStringCellValue());
                        customer.setAddress(row.getCell(2).getStringCellValue());
                        customer.setCity(row.getCell(3).getStringCellValue());
                        customer.setPhone(row.getCell(5).getStringCellValue().replace("/", "").replace("-", ""));

                        //login.setUsername(authentication.getName());
                        customer.setUsers(login);
                        Orders order = ordersRepository.findOrdersByOrderId(row.getCell(0).getStringCellValue());
                        if(order == null) {
                            order = new Orders();
                            order.setShipmentNumber(file.getOriginalFilename());
                            order.setOrderId(row.getCell(0).getStringCellValue());
                            // when parsing, if finds ambiguous CET or CEST, it uses Berlin as prefered timezone
                            Set<ZoneId> set = new HashSet<>();
                            set.add(ZoneId.of("Europe/Berlin"));

                            DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                                    // your pattern (weekday, month, day, hour/minute/second)
                                    .appendPattern("EE MMM dd HH:mm:ss ")
                                    // optional timezone short name (like "CST" or "CEST")
                                    .optionalStart().appendZoneText(TextStyle.SHORT, set).optionalEnd()
                                    // optional GMT offset (like "GMT+02:00")
                                    .optionalStart().appendPattern("OOOO").optionalEnd()
                                    // year
                                    .appendPattern(" yyyy")
                                    // create formatter (using English locale to make sure it parses weekday and month names correctly)
                                    .toFormatter(Locale.US);
                            //DateTimeFormatter formater = DateTimeFormatter.ofPattern("E dd MMM z yyyy HH:mm:ss", Locale.ENGLISH);
                            //ZonedDateTime parsedDate = ZonedDateTime.parse(row.getCell(19).getDateCellValue().toString(), formatter);
                            //LocalDateTime z1 = LocalDateTime.parse(row.getCell(19).getDateCellValue().toString(), fmt);

                            order.setOrderSent(LocalDateTime.parse(row.getCell(19).getDateCellValue().toString(), fmt));
                            order.setCustomers(customer);
                            order.setStatus(0);

                            //orders.add(order);
                            //customer.setOrders(orders);
                            ordersRepository.save(order);
                        }
                    }
                }
            }
            files.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }
}
