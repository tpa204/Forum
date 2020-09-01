package telran.ashkelon2020.accounting.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(50)
public class ValidateAuthorFilter implements Filter{


	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (checkPathAndMethod(path, method)) {
			String user = request.getUserPrincipal().getName();
			String[] arr = path.split("/");
			String author = arr[arr.length - 1];
			if (!(user.equals(author))) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}
	
	private boolean checkPathAndMethod(String path, String method) {
		boolean res = path.matches("/forum/post/\\w+/?")
				&& ("POST".equalsIgnoreCase(method));
		res = res || path.matches("/forum/post/\\w+/comment/\\w+/?")
				&& ("PUT".equalsIgnoreCase(method));
		return res;
	}

}
