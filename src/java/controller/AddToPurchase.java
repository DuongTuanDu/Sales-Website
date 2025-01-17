package controller;

import dal.ordersDAO;
import dal.productDAO;
import dal.purchaseDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import model.Orders;
import model.Product;
import model.Purchase;
import model.Users;

/**
 *
 * @author GIA KHANH
 */
public class AddToPurchase extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ordersDAO orderDao = new ordersDAO();
        purchaseDAO purchaseDao = new purchaseDAO();
        productDAO p = new productDAO();
        //DOC TU SESSION RA GIO HANG TRUOC
        HttpSession session = request.getSession();
        Object obj = session.getAttribute("cart");// luu tam vao session
        int countOrder = 0;
        if (obj != null) {//KIEM TRA XEM CO SP TRONG GIO HANG KO?
            Map<String, Orders> map = (Map<String, Orders>) obj;

            //TAO HOA DON TRUOC, DE LAY DUOC ID BILL
            Purchase purchase = new Purchase();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            purchase.setDate(sdf.format(new Date()).toString());
            purchase.setDate(sdf.format(new java.util.Date()));
            // lay dang nhap
            Users user = (Users) session.getAttribute("account");
            purchase.setUser(user);

            //XEM THEM O LOP BILLDAO, CACH TRA VE ID TU GEN O SQL
            int purchaseId = purchaseDao.createPurchase(user.getId(), 0, purchase.getDate());// save bill trc de lay id

            // Tim mat hang
            double total = 0;//tinh tong gia

            //lap cac phan tu trong map
            for (Entry<String, Orders> entry : map.entrySet()) {
                Orders order = entry.getValue();

                order.setPurchase(purchase);// set bill id vao day
                //luu lai cac mat hang
                orderDao.createOrder(order.getProduct().getId(), order.getQuantity(), purchaseId, order.getPrice());
                // tinh tong gia
                total += order.getQuantity() * order.getPrice();

                // update quantity product
                int quantityP = order.getProduct().getQuantity() - order.getQuantity();
                order.getProduct().setQuantity(quantityP);
                p.updateQuantityProduct(quantityP, order.getProduct().getId());
            }

            ///cap nhat lai bill de co tong gia tien
            purchase.setTotal(total);
            purchaseDao.updatePurchase(total, purchaseId);

            // chuyen ve trang thanh cong
            // xoa session gio hang vi da tao don hang thanh cong, giai phong bo nho
            session.removeAttribute("cart");
            session.setAttribute("countOrder", countOrder);
            response.sendRedirect("thankyou.jsp");
        } else {
            // tra ve trang chu neu gio hang rong
            session.setAttribute("countOrder", countOrder);
            response.sendRedirect("home");
        }
    }

}