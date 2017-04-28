package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.ProjectNoticeMapper;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

@Component
public class ProjectNoticeManagerImpl implements ProjectNoticeManager {
	
	@Autowired
	private ProjectNoticeMapper projectNoticeMapper;

	@Override
	public Page<ProjectNotice> findByPage(Page<ProjectNotice> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = projectNoticeMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<ProjectNotice> selectForPagin = projectNoticeMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertProjectNotice(ProjectNotice notice) throws ManagerException {
		try {
			return projectNoticeMapper.insertSelective(notice);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateProjectNotice(ProjectNotice notice)
			throws ManagerException {
		try {
			return projectNoticeMapper.updateByPrimaryKeySelective(notice);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteProjectNoticeById(Long id) throws ManagerException {
		try {
			return projectNoticeMapper.deleteProjectNoticeById(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int autoStartProjectNoticeTask() throws ManagerException {
		try {
			return projectNoticeMapper.autoStartProjectNoticeTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int autoEndProjectNoticeTask() throws ManagerException {
		try {
			return projectNoticeMapper.autoEndProjectNoticeTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectNotice findProjectNoticeById(Long id) throws ManagerException {
		try {
			return projectNoticeMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateProjectNoticeStatus(int newStatus, int currentStatus,
			Long id) throws ManagerException {
		try {
			return projectNoticeMapper.updateProjectNoticeStatus(newStatus, currentStatus, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int recommendProjectNotice(Long id) throws ManagerException {
		try {
			return projectNoticeMapper.recommendProjectNotice(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelRecommendProjectNoticeById(Long id) throws ManagerException {
		try {
			return projectNoticeMapper.cancelRecommendProjectNoticeById(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelRecommendProjectNotice() throws ManagerException {
		try {
			return projectNoticeMapper.cancelRecommendProjectNotice();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectNoticeForFront> getProjectNoticeForFront(int num)
			throws ManagerException {
		try {
			return projectNoticeMapper.getProjectNoticeForFront(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectNoticeForFront> p2pGetProjectNoticeForFront(int num)
			throws ManagerException {
		try {
			return projectNoticeMapper.p2pGetProjectNoticeForFront(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public ProjectNoticeForFront getProjectNoticeByIndexShow()
			throws ManagerException {
		try {
			return projectNoticeMapper.getProjectNoticeByIndexShow();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ProjectNoticeForFront p2pGetProjectNoticeByIndexShow()
			throws ManagerException {
		try {
			return projectNoticeMapper.p2pGetProjectNoticeByIndexShow();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateProjectNoticeSort(Long id, int sort)
			throws ManagerException {
		try {
			return projectNoticeMapper.updateProjectNoticeSort(id, sort);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int resetProjectNoticeSort(int sort) throws ManagerException {
		try {
			return projectNoticeMapper.resetProjectNoticeSort(sort);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectNotice getProjectNoticeBySortIndex(int index)
			throws ManagerException {
		try {
			return projectNoticeMapper.getProjectNoticeBySortIndex(index-1);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectNotice> findUpcomingProjectNotice()
			throws ManagerException {
		try {
			return projectNoticeMapper.findUpcomingProjectNotice();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectNotice getProjectNoticingByProjectId(Long projectId)
			throws ManagerException {
		try {
			return projectNoticeMapper.getProjectNoticingByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectNotice getSortFirstProjectNotice() throws ManagerException {
		try {
			return projectNoticeMapper.getSortFirstProjectNotice();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ProjectNotice p2pGetSortFirstProjectNotice() throws ManagerException {
		try {
			return projectNoticeMapper.p2pGetSortFirstProjectNotice();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
