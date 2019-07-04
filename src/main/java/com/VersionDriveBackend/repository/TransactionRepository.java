/*
* TransactionRepository
*  This Interface is a repository for storing Transaction detailed objects
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.TransactionManagementStuff;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionManagementStuff, Long>{
	
	@Query(value="SELECT * FROM transaction_table where userid=?  ORDER BY creation_date  DESC" , nativeQuery=true)
	public List<TransactionManagementStuff> getAllByuserid(long userid);
	
	
}
