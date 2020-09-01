package telran.ashkelon2020.accounting.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2020.accounting.dto.exceptions.ForbiddenException;
import telran.ashkelon2020.accounting.dto.exceptions.UserNotFoundException;
import telran.ashkelon2020.accounting.service.security.AccountSecurity;

@Service
@Order(20)
public class ExpDateFilter implements Filter {

	@Autowired
	AccountSecurity securityService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (checkPathAndMethod(path, method)) {
			try {
				securityService.checkExpDate(request.getUserPrincipal().getName());
				boolean res = securityService.isBanned(request.getUserPrincipal().getName());
				if (res) {
					response.sendError(403, "user banned");
					return;
				}
			} catch (ForbiddenException e) {
				response.sendError(403);
				return;
			} catch (UserNotFoundException e) {
				response.sendError(404);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkPathAndMethod(String path, String method) {
		boolean res = "/account/login".equalsIgnoreCase(path) && "Post".equalsIgnoreCase(method);
		res = res || ("Put".equalsIgnoreCase(method) && path.matches("/account/user/\\w+/?"));
		boolean forum = path.startsWith("/forum") 
				&& (!"GET".equalsIgnoreCase(method))
				&& (!path.matches("/forum/posts/(tags|period)/?"));
		res = res || forum;	
		return res;
	}

}
