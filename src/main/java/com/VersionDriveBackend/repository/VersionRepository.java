/*
* VersionRepository
*  This Interface is a repository for storing Versions of file detailed objects
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.VersionStuff;




@Repository
public interface VersionRepository extends JpaRepository<VersionStuff,Long>{

}
