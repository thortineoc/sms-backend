package com.sms.usermanagementservice.control;

import com.sms.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sms.usermanagementservice.control.UserMapper.toDTO;

public class FilteredUsers {

    public List<UserRepresentation> userRepresentation =  new ArrayList<>();
    public List<UserDTO> usersDTOList = new ArrayList<>();

    // input: role I output Ia Ic IIc
    //it works but...
    List<UserRepresentation> filterUsersByParam(List<UserRepresentation> users, QueryParams parameters){

        if(parameters.getGroup()!=null)
            userRepresentation = users.stream().filter((f) -> f.getAttributes().get("group").
                    toString().contains(parameters.getGroup())).collect(Collectors.toList());
        if(parameters.getMiddleName()!=null)
            userRepresentation = users.stream().filter((f) -> f.getAttributes().get("middleName").
                    toString().contains(parameters.getMiddleName())).collect(Collectors.toList());
        if(parameters.getPhoneNumber()!=null)
            userRepresentation = users.stream().filter((f) -> f.getAttributes().get("phoneNumber").
                    toString().contains(parameters.getPhoneNumber())).collect(Collectors.toList());
        if(parameters.getPesel()!=null)
            userRepresentation = users.stream().filter((f) -> f.getAttributes().get("pesel").
                    toString().contains(parameters.getPesel())).collect(Collectors.toList());

        return  userRepresentation;
    }

    private void toUserDTO(){
        for(UserRepresentation user : userRepresentation)
            usersDTOList.add(toDTO(user));
    }


}
