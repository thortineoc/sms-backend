package com.sms.usermanagementservice.control;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("request")
public class UsersService {

 public void getAllUsers(){
     UsersRepository.findAll();
 }

 public void getUser(long id){
     UsersRepository.findUserById(id);
 }

 public void getGroup(short groupID){
     UsersRepository.findByGroupId(groupID);
 }

 public void getTeachers(){
     UsersRepository.findByRole("teacher");
 }

 public void getTeacher(long id){
     UsersRepository.findById(id);
 }

 public void getUsersByRole(String role){
     UsersRepository.findByRole(role);
 }

 public void getNUsersByRole(String role, int number){

     List<User> list= new ArrayList<User>(10);
     List<User> tmp=UsersRepository.findByRole(role);
     for(int i=number-10; i<number; i++){
         list.add(tmp.get(i));
     }
 }
}
