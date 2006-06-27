package bugeater.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import bugeater.dao.AttachmentDao;

import bugeater.domain.Attachment;
import bugeater.domain.Issue;

import bugeater.service.AttachmentService;

/**
 * An implementation of the bugeater.service.AttachmentService
 * interface.
 * 
 * @author pchapman
 */
public class AttachmentServiceImpl implements AttachmentService
{
	/**
	 * Creates a new instance.
	 */
	public AttachmentServiceImpl()
	{
		super();
	}

	/* Spring injected */
	private AttachmentDao aDao;
	public void setAttachmentDao(AttachmentDao dao)
	{
		this.aDao = dao;
	}
	
    /* Spring Injected */
    private File attachmentDir;
    public void setAttachmentDir(String dir)
    {
    	attachmentDir = new File(dir);
    }
    
    /**
     * @see @see bugeater.service.AttachmentService#getAttachments(Issue)
     */
    public List<Attachment>getAttachments(Issue i)
    {
    	return aDao.getAttachments(i);
    }

	/**
	 * @see bugeater.service.AttachmentService#getAttachment(bugeater.domain.Attachment)
	 */
	public byte[] getData(Attachment attachment) throws IOException
	{
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		byte[] retValue = null;
		
		try {
			// Open the file, then read it in.
			outStream = new ByteArrayOutputStream();
			inStream =
				new FileInputStream(new File(attachment.getStorageName()));
			copy(inStream, outStream);
			outStream.close();
			outStream = null;
			retValue = outStream.toByteArray();
		} finally {
			if (inStream != null) {
				try { inStream.close(); } catch (IOException ioe2) {}
			}
			if (outStream != null) {
				try { outStream.close(); } catch (IOException ioe2) {}
			}
		}
		return retValue;
	}

	/**
	 * @see bugeater.service.AttachmentService#getLastModifyTime(bugeater.domain.Attachment)
	 */
	public Calendar getLastModifyTime(Attachment attachment)
	{
		File f = new File(attachment.getStorageName());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(f.lastModified());
		return cal;
	}
    
    // METHODS
	/**
	 * @see bugeater.service.AttachmentService#load(Long)
	 */
	public Attachment load(Long id)
	{
		return aDao.load(id);
	}

	/**
	 * @see bugeater.service.AttachmentService#save(bugeater.domain.Attachment, java.io.InputStream)
	 */
	public void save(Attachment attachment, InputStream iStream)
		throws IOException
	{
    	// Create a unique name for the file in the image directory and
    	// write the image data into it.
    	File newFile = createStorageFile();
    	OutputStream outStream = null;
    	try {
	    	outStream = new FileOutputStream(newFile);
			copy(iStream, outStream);
	    	outStream.close();
    	} finally {
    		if (outStream != null) {
    			try { outStream.close(); } catch (IOException ioe) {}
    		}
    	}
    	attachment.setStorageName(newFile.getAbsolutePath());
    	
    	// Save the image info in the database
        aDao.save(attachment);
	}
    
	/**
	 * Creates a File object with a unique name within the storage directory.
	 */
    private File createStorageFile()
    {
    	UUID uuid = UUID.randomUUID();
    	File file = new File(attachmentDir, uuid.toString() + uuid.toString());
    	return file;
    }

    /**
     * Copies data from src into dst.
     */
    private void copy(InputStream source, OutputStream destination)
    	throws IOException
    {
    	try {
	        // Transfer bytes from source to destination
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = source.read(buf)) > 0) {
	            destination.write(buf, 0, len);
	        }
	        source.close();
	        destination.close();
    	} catch (IOException ioe) {
    		throw ioe;
    	}
    }
}
