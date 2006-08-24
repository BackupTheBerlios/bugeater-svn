package bugeater.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A lookup value stored in the database.
 * 
 * @author pchapman
 */
@Entity
@Table (name = "be_lookup_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LookupValue
{
	public enum ValueType {
		Category, Project
	}
	
	/**
	 * Creates a new empty instance.
	 */
	protected LookupValue()
	{
		super();
	}
	
	/**
	 * Creates a new instance with the given type and value.
	 * <b>Note:</b> This constructor is not public API.  It should only be
	 * called from the Service layer.
	 */
	public LookupValue(ValueType type, String value)
	{
		super();
		this.type = type;
		this.value = value;
	}
	
	/**
	 * the unique ID of the lookup value.
	 */
	@Id @GeneratedValue
	@Column(name="value_id")
	private Long id;
	protected Long getId()
	{
		return id;
	}
	protected LookupValue setId(Long id)
	{
		this.id = id;
		return this;
	}

	/**
	 * The type of the lookup value.  Basically, what field that the value
	 * will be used to fill.
	 */
	@Column( name="type", nullable=false )
	private ValueType type;
	public ValueType getType()
	{
		return type;
	}
	protected LookupValue setType(ValueType type)
	{
		this.type = type;
		return this;
	}

	/**
	 * The value.
	 */
	@Column( name="value", nullable=false )
	private String value;
	public String getValue()
	{
		return value;
	}
	public LookupValue setValue(String value)
	{
		this.value = value;
		return this;
	}
}
