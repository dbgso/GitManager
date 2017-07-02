package com.github.dbgso.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface GitProjectRepository extends PagingAndSortingRepository<GitProject, Long> {

	public GitProject findByName(@Param("name") String name);
}
