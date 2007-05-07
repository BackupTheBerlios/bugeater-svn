package bugeater.hibernate;

import java.util.List;

import bugeater.dao.LookupValueDao;
import bugeater.domain.LookupValue;
import bugeater.domain.LookupValue.ValueType;

/**
 * An implementation of the LookupValueDao interface.
 * 
 * @author pchapman
 */
public class LookupValueDaoImpl extends AbstractHibernateDao<LookupValue>
	implements LookupValueDao
{
	/**
	 * @param dataClass
	 */
	public LookupValueDaoImpl()
	{
		super(LookupValue.class);
	}

	/**
	 * @see bugeater.hibernate.AbstractHibernateDao#delete(T)
	 */
	@Override
	public void delete(LookupValue persistedObject)
	{
		super.delete(persistedObject);
	}



	/**
	 * @see bugeater.dao.LookupValueDao#load(bugeater.domain.LookupValue.ValueType)
	 */
	@SuppressWarnings("unchecked")
	public List<LookupValue> load(ValueType type)
	{
		return
			(List<LookupValue>)getSession().createQuery(
					"select lv " +
					"from LookupValue lv " +
					"where lv.type = :type " +
					"order by lv.value"
				).setParameter("type", type).list();
	}

	/**
	 * @see bugeater.dao.LookupValueDao#load(bugeater.domain.LookupValue.ValueType, java.lang.String)
	 */
	public LookupValue load(ValueType type, String value)
	{
		return
			(LookupValue)getSession().createQuery(
					"select lv " +
					"from LookupValue lv " +
					"where lv.type = :type and lv.value = :value"
				)
			.setParameter("type", type).setParameter("value", value).uniqueResult();
	}
}
