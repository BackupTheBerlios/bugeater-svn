package bugeater.web.page;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import bugeater.service.AuthenticationException;
import bugeater.service.AuthenticationService;
import bugeater.web.BugeaterSession;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.Session;

import wicket.markup.html.form.Form;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.FormComponentFeedbackBorder;

import wicket.model.IModel;
import wicket.model.Model;

import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequestCycle;

import wicket.spring.injection.SpringBean;

/**
 * A page which allows the user to log in.
 * 
 * @author pchapman
 */
public class LoginPage extends BugeaterPage
{
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(LoginPage.class);
	
	@SpringBean
	private AuthenticationService authenticationService;
	public void setAuthenticationService(AuthenticationService service)
	{
		this.authenticationService = service;
	}
	
	/**
	 * Creates a new login page. 
	 */
	public LoginPage()
	{
		super();
		new LoginForm(this, "form");
	}
	
	private class LoginForm extends Form
	{
		private static final long serialVersionUID = 1L;
		
		public LoginForm(MarkupContainer parent, String wicketID)
		{
			super(parent, wicketID);

			new FormComponentFeedbackBorder(LoginForm.this, "feedback");
			
			loginModel = new Model<String>();
			new TextField<String>(
					LoginForm.this, "username", loginModel
				);
			passModel = new Model<String>();
			new PasswordTextField(
					LoginForm.this, "password", passModel
				);
		}
		
		private IModel<String>loginModel;
		private IModel<String>passModel;
		
		private String getLogin()
		{
			String s = loginModel.getObject();
			return s == null ? "" : s;
		}
		
		private String getPassword()
		{
			String s = passModel.getObject();
			return s == null ? "" : s;
		}
		
		public void onSubmit()
		{
			try {
				// Attempt authentication
				WebRequestCycle cycle = WebRequestCycle.get();
				HttpServletRequest request = cycle.getWebRequest().getHttpServletRequest();
				HttpServletResponse response = cycle.getWebResponse().getHttpServletResponse();
				ServletContext context = ((WebApplication)Application.get()).getServletContext();
				Principal p =
					authenticationService.login(
							request, response, context, getLogin(), getPassword()
						);
				if (p != null) {
					// The authentication succeded.  Set it in the session.
					((BugeaterSession)Session.get()).setPrincipal(p);
					
					// If login has been called because the user was not yet
					// logged in, than continue to the original destination,
					// otherwise to the Home page
					if (getPage().continueToOriginalDestination())
					{
						// HTTP redirect response has been committed. No more data
						// shall be written to the response.
					} else {
						getPage().setResponsePage(
								WebApplication.get().getHomePage()
							);
					}
				}
			} catch (ServletException se) {
				logger.error(se);
			} catch (AuthenticationException ae) {
				// The reason will give a key used to
				// look up the localized message.
				error(getString(ae.getReason().getDescriptionKey()));
			}
		}
	}
}
