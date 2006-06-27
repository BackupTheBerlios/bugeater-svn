package bugeater.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import bugeater.bean.ReleaseVersionBean;
import bugeater.domain.ReleaseVersion;

/**
 * An interface that defines methods for manipulating ReleaseVersionBean objects.
 * 
 * @author pchapman
 */
public interface ReleaseVersionService
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
	@Transactional
	public void save(ReleaseVersionBean release);
}
