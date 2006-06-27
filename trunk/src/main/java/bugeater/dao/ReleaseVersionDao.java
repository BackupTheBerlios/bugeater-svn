package bugeater.dao;

import java.util.List;

import bugeater.domain.ReleaseVersion;
import bugeater.service.SortOrder;

/**
 * An interface that defines data access methods for ReleaseVersionBean objects.
 * 
 * @author pchapman
 */
public interface ReleaseVersionDao
{
	/**
	 * A list of releases for the project.
	 */
	public List<ReleaseVersion>loadAll(String project, SortOrder order);

	/**
	 * Loads a specific release by ID.
	 */
	public ReleaseVersion load(Long id);

	/**
	 * Saves changes to the release.
	 */
	public void save(ReleaseVersion release);
}
