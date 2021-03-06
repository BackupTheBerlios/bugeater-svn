package bugeater.web;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import bugeater.domain.Attachment;
import bugeater.service.AttachmentService;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.DynamicWebResource;

import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.value.ValueMap;

/**
 * A resource that provides attachments from the holding dir.
 *
 * @author pchapman
 */
public class AttachmentResource extends DynamicWebResource
{
	// CONSTANTS

	public static final Log logger =
		LogFactory.getLog(AttachmentResource.class);

	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS

	public AttachmentResource()
	{
		super();
	}

	public AttachmentResource(Locale local)
	{
		super(local);
	}

	// METHODS

	// MEMBERS

	private transient AttachmentService attachmentService;
	/* Spring injected */
	private AttachmentService getAttachmentService()
	{
		if (attachmentService == null) {
			attachmentService =
				(AttachmentService)((BugeaterApplication)Application.get())
				.getSpringBean("attachmentService");
		}
		return attachmentService;
	}
	
    /**
     * Loads the image entry by the Id stored in the parameters, or null if
     * no Id was provided.
     * @return The image entry.
     */
    private Attachment getAttachment(ValueMap params)
    {
    	Attachment attachment = null;
    	try {
	    	if (params.containsKey(BugeaterConstants.PARAM_NAME_ATTACHMENT_ID)) {
	    		attachment = getAttachmentService().load(params.getLong(BugeaterConstants.PARAM_NAME_ATTACHMENT_ID));
	    	}
	    	return attachment;
        } catch (Exception e) {
        	logger.error(e);
        	return null;
        }
    }

    /**
     * @see DynamicWebResource#getResourceState()
     */
	@Override
	protected ResourceState getResourceState()
	{
		Attachment a = getAttachment(getParameters());
    	if (a == null) {
    		return new ResourceState()
    		{
				/**
				 * @see wicket.markup.html.DynamicWebResource.ResourceState#getContentType()
				 */
				@Override
				public String getContentType()
				{
					return "text/plain";
				}

				/**
				 * @see wicket.markup.html.DynamicWebResource.ResourceState#getData()
				 */
				@Override
				public byte[] getData()
				{
					return new byte[0];
				}

				/**
				 * @see wicket.markup.html.DynamicWebResource.ResourceState#getLength()
				 */
				@Override
				public int getLength()
				{
					return 0;
				}

				private Time time = Time.now();
				
				/**
				 * @see wicket.markup.html.DynamicWebResource.ResourceState#lastModifiedTime()
				 */
				@Override
				public Time lastModifiedTime()
				{
					return time;
				}
    		};
    	}
		AttachmentResourceState state =
			new AttachmentResourceState(
					Time.valueOf(
							getAttachmentService().getLastModifyTime(a).getTime()
						)
			);
		state.setContentType(a.getContentType());
        try {
    		state.setData(getAttachmentService().getData(a));
        } catch (Exception e) {
        	logger.error(e);
        }

		return state;
	}
	
	class AttachmentResourceState extends ResourceState
	{
		// CONSTRUCTORS
		
		AttachmentResourceState(Time lastModified)
		{
			super();
			this.lastModified = lastModified;
		}
		
		// MEMBERS
		
		private String contentType;
		@Override
		public String getContentType()
		{
			return contentType;
		}
		void setContentType(String contentType)
		{
			this.contentType = contentType;
		}

		private byte[] data;
		@Override
		public byte[] getData()
		{
			return data;
		}
		void setData(byte[] data)
		{
			this.data = data;
		}

		@Override
		public int getLength()
		{
			return data.length;
		}

		private Time lastModified;
		@Override
		public Time lastModifiedTime()
		{
			return lastModified;
		}
		
		// METHODS
	}
}
