package ren.shuaipeng.sanbox.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IndexService indexService = new IndexService();
        indexService.get(req.getParameter("a"));
        PrintWriter writer = resp.getWriter();
        writer.write("welcome,sanbox");
        writer.flush();
        writer.close();
    }
}
