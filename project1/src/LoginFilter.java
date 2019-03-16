//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
//
///**
// * Servlet Filter implementation class LoginFilter
// */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
//public class LoginFilter implements Filter {
//
//    /**
//     * Default constructor. 
//     */
//    public LoginFilter() {
//        // TODO Auto-generated constructor stub
//    }
//    
//    /**
//	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//	 */
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		// place your code here
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse) response;
//		
//		System.out.println("LoginFilter: " + httpRequest.getRequestURI());
//		
//		if(this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//			chain.doFilter(request, response);
//			return;
//		}
//		
//		if (httpRequest.getSession().getAttribute("user") == null && httpRequest.getSession().getAttribute("employee") == null) {
//			httpResponse.sendRedirect("login.html");
//		} else if(httpRequest.getSession().getAttribute("user") != null && httpRequest.getSession().getAttribute("employee") == null && httpRequest.getRequestURI().endsWith("__dashboard.html")) {
//			httpResponse.sendRedirect("index.html");
//		} //else if(httpRequest.getSession().getAttribute("employee") == null && httpRequest.getRequestURI().endsWith("__dashboard.html")) {
////			httpResponse.sendRedirect("login.html");
////		}
//		else {
//			chain.doFilter(request, response);
//		}
//
//		
//		// pass the request along the filter chain
//	}
//
//	private boolean isUrlAllowedWithoutLogin(String requestURI) {
//		requestURI = requestURI.toLowerCase();
//		return requestURI.endsWith("login.html") || 
//				requestURI.endsWith("login.js") ||
//				requestURI.endsWith("api/login") ||
//				requestURI.endsWith("login.css") ||
//				requestURI.endsWith("images/movie.png");
//				
//	}
//	
//	/**
//	 * @see Filter#destroy()
//	 */
//	public void destroy() {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see Filter#init(FilterConfig)
//	 */
//	public void init(FilterConfig fConfig) throws ServletException {
//		// TODO Auto-generated method stub
//	}
//
//}
