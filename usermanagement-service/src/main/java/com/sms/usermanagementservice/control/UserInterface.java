package com.sms.usermanagementservice.control;


import org.postgresql.core.Query;

public interface UserInterface extends JpaRepository<User, Integer> {

    List<User> findById(int ID); //po id

    @Query("Select u from Users u WHERE u.group_id=?1") //po grupie
    User findByGroupId(int ID);

    @Query("Select u from Users u WHERE u.role=?1") //po roli
    List<User> findByRole(String role);






}

