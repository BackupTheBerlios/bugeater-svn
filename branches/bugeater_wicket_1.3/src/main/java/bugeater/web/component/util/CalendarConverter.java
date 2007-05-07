package bugeater.web.component.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.util.convert.converters.AbstractConverter;

/**
 * Converts from Object to Calendar.  Adapted from wicket's CalendarConverter
 * 
 * @author Eelco Hillenius
 * @author pchapman
 */
public class CalendarConverter extends AbstractConverter
{
	private static final long serialVersionUID = 1L;

	/** The date format to use for the specific locales (used as the key) */
	private final Map<Locale, DateFormat> dateFormats = new HashMap<Locale, DateFormat>();

	/**
	 * Specify whether or not date/time parsing is to be lenient. With lenient
	 * parsing, the parser may use heuristics to interpret inputs that do not
	 * precisely match this object's format. With strict parsing, inputs must
	 * match the object's format.
	 */
	private final boolean lenient;

	/**
	 * Construct. Lenient is false.
	 */
	public CalendarConverter()
	{
		super();
		lenient = false;
	}

	/**
	 * Construct.
	 * 
	 * @param lenient
	 *            when true, parsing is lenient. With lenient parsing, the
	 *            parser may use heuristics to interpret inputs that do not
	 *            precisely match this object's format. With strict parsing,
	 *            inputs must match the object's format.
	 */
	public CalendarConverter(boolean lenient)
	{
		super();
		this.lenient = lenient;
	}

	/**
	 * @see wicket.util.convert.IConverter#convertToObject(java.lang.String,Locale)
	 */
	public Object convertToObject(final String value, Locale locale)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date)parse(getDateFormat(locale), value, locale));
		return cal;
	}

	/**
	 * @see wicket.util.convert.IConverter#convertToString(java.lang.String,
	 *      Locale)
	 */
	@Override
	public String convertToString(final Object value, Locale locale)
	{
		final DateFormat dateFormat = getDateFormat(locale);
		if (dateFormat != null)
		{
			return dateFormat.format(((Calendar)value).getTime());
		}
		return value.toString();
	}


	/**
	 * @param locale
	 * @return Returns the date format.
	 */
	public DateFormat getDateFormat(Locale locale)
	{
		DateFormat dateFormat = dateFormats.get(locale);
		if (dateFormat == null)
		{
			dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			dateFormat.setLenient(lenient);
			dateFormats.put(locale, dateFormat);
		}
		return dateFormat;
	}

	/**
	 * @param locale
	 * @param dateFormat
	 *            The dateFormat to set.
	 */
	public void setDateFormat(final Locale locale, final DateFormat dateFormat)
	{
		this.dateFormats.put(locale, dateFormat);
	}

	/**
	 * @see wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	@Override
	protected Class<Calendar> getTargetType()
	{
		return Calendar.class;
	}
}