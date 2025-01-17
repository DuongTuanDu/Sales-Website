/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.productDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import model.Orders;
import model.Product;

/**
 *
 * @author GIA KHANH
 */
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        productDAO pDao = new productDAO();
        int idC = Integer.parseInt(id);
        Product product = pDao.getProductById(idC);
        double total = 0;

        HttpSession session = request.getSession();
        Object obj = session.getAttribute("cart");// luu tam vao session
        Object objCountOrder = session.getAttribute("countOrder");
        int countOrder;
        if (objCountOrder == null) {
            countOrder = 0;
        } else {
            countOrder = (int) objCountOrder;
        }
        if (obj == null) {// tao moi
            // Tao mat hang
            Orders order = new Orders();
            order.setProduct(product);
            order.setQuantity(1);
            order.setPrice(product.getPrice());
            // gio hang co nhieu mat hang
            Map<String, Orders> map = new HashMap<>();
            map.put(id, order);// them mat hang vao ds
            total += product.getPrice();
            countOrder++;
            session.setAttribute("countOrder", countOrder);
            session.setAttribute("total", total);
            session.setAttribute("cart", map);// luu tam vao session
        } else {
            Map<String, Orders> map = (Map<String, Orders>) obj;

            Orders order = map.get(id);

            if (order == null) {
                order = new Orders();
                order.setProduct(product);
                order.setQuantity(1);
                order.setPrice(product.getPrice());
                map.put(id, order);
                countOrder++;
            } else {
                order.setQuantity(order.getQuantity() + 1);
            }
            for (Map.Entry<String, Orders> entry : map.entrySet()) {
                total += entry.getValue().getQuantity() * entry.getValue().getPrice(); // Tính tổng
            }
            session.setAttribute("total", total);
            session.setAttribute("cart", map);// luu tam vao session
            session.setAttribute("countOrder", countOrder);
        }
        response.sendRedirect("shop");
    }

}
