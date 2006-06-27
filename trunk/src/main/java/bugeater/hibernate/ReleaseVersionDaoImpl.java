package bugeater.hibernate;

import java.util.List;

import bugeater.dao.ReleaseVersionDao;
import bugeater.domain.ReleaseVersion;
import bugeater.service.SortOrder;

/**
 * An implementation of the bugeater.dao.ReleaseVersionDao that uses hibernate.
 * 
 * @author pchapman
 */
public class ReleaseVersionDaoImpl extends AbstractHibernateDao<ReleaseVersion>
	implements ReleaseVersionDao
{
	/**
	 * @param dataClass
	 */
	public ReleaseVersionDaoImpl()
	{
		super(ReleaseVersion.class);
	}

	/**
	 * @see bugeater.dao.ReleaseVersionDao#loadAll(java.lang.String, bugeater.service.SortOrder)
	 */
	@SuppressWarnings("unchecked")
	public List<ReleaseVersion> loadAll(String project, SortOrder order)
	{
		return (List<ReleaseVersion>)getSession().createQuery(
				"select rv " +
				"from ReleaseVersion rv " +
				"where rv.project = :project " +
				"order by rv.versionNumber " + order.getSqlToken()
			)
			.setParameter("project", project).list();
	}
}
