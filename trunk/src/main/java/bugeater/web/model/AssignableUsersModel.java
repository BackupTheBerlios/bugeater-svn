package bugeater.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bugeater.bean.IUserBean;
import bugeater.service.SecurityRole;
import bugeater.service.UserService;
import bugeater.web.BugeaterApplication;

import wicket.Application;
import wicket.model.AbstractDetachableModel;

/**
 * A model that provides a list of users that an issue may be assigned to.
 * 
 * @author pchapman
 */
public class AssignableUsersModel extends AbstractDetachableModel<List<IUserBean>>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance.
	 */
	public AssignableUsersModel()
	{
		super();
	}
	
	private List<IUserBean>list;

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	@Override
	protected void onAttach()
	{
		if (list == null) {
			UserService service =
				(UserService)((BugeaterApplication)Application.get()).getSpringBean("userService");
			Set<IUserBean>set = new HashSet<IUserBean>();
			Set<IUserBean>s = null;
			s = service.getUsersByRole(SecurityRole.Developer);
			set.addAll(s);
			s = service.getUsersByRole(SecurityRole.Tester);
			set.addAll(s);
			list = new ArrayList<IUserBean>();
			list.addAll(set);
			Collections.sort(
					list,
					new Comparator<IUserBean>()
					{
						public int compare(IUserBean o1, IUserBean o2)
						{
							int i = o1.getLastname().compareTo(o2.getLastname());
							if (i == 0) {
								i = o1.getFirstname().compareTo(o2.getFirstname());
							}
							return i;
						}
					}
			);
			list.add(0, null);
		}
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	@Override
	protected void onDetach()
	{
		list = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject()
	 */
	@Override
	protected List<IUserBean> onGetObject()
	{
		return list;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(T)
	 */
	@Override
	protected void onSetObject(List<IUserBean> object)
	{
		// Not implemented			
	}
}
