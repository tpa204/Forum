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
import telran.ashkelon2020.forum.dao.PostRepository;
import telran.ashkelon2020.forum.model.Post;

@Service
@Order(60)
public class ValidateAuthorOrModeratorFilter implements Filter {
	
	@Autowired
	PostRepository postRepository;
	
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
			String user = request.getUserPrincipal().getName();
			String postId = path.split("/")[3];
			Post post = postRepository.findById(postId).orElse(null);
			if (post == null) {
				response.sendError(404, "post id = " + postId + " not found");
				return;
			}
			String author = post.getAuthor();
			if (!(user.equals(author) || securityService.checkHaveRole(user, "Moderator"))) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}
	
	private boolean checkPathAndMethod(String path, String method) {
		boolean res = path.matches("/forum/post/\\w+/?")
				&& ("PUT".equalsIgnoreCase(method) || ("DELETE".equalsIgnoreCase(method)));
		return res;
	}

}
