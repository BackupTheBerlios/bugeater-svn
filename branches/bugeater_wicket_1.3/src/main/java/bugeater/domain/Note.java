package bugeater.domain;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author pchapman
 *
 */
@Entity
@Table (name = "be_note")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Note
{
	/**
	 * A note for an issue.
	 */
	public Note()
	{
		super();
		createTime = Calendar.getInstance();
	}
	
	@Id @GeneratedValue
	@Column(name="note_id")
	private Long id;
	public Long getId()
	{
		return id;
	}
	protected Note setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	@Column( name="note_text", length=32768 )
	private String text;
	public String getText()
	{
		return text;
	}
	public Note setText(String text)
	{
		this.text = text;
		return this;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="issue_id")
    private Issue issue;
	public Issue getIssue()
	{
		return issue;
	}
	// returns this for chaining.
	public Note setIssue(Issue issue)
	{
		this.issue = issue;
		return this;
	}

	@Column( name="user_id", nullable=false )
    private String userID;
	public String getUserID()
	{
		return userID;
	}
	// returns this for chaining.
	public Note setUserID(String userID)
	{
		this.userID = userID;
		return this;
	}
	
	@Column(name = "create_time", nullable=false)
	private Calendar createTime;
	public Calendar getCreateTime()
	{
		return createTime;
	}
}
