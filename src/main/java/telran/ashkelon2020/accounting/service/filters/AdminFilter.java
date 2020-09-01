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

import telran.ashkelon2020.accounting.service.security.AccountSecurity;

@Service
@Order(40)
public class AdminFilter implements Filter {
	
	@Autowired
	AccountSecurity securityService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		if (checkPathAndMethod(path)) {
			String user = request.getUserPrincipal().getName();
			boolean res = securityService.checkHaveRole(user, "Administrator");
			if (!res) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);

	}
	
	private boolean checkPathAndMethod(String path) {
		boolean res = path.matches("/account/user/\\w+/role/[A-Za-z]+/?");
		return res;
	}

}
