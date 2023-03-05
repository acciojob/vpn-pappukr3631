package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        //Connect the user to a vpn by considering the following priority order.
        //1. If the user is already connected to any service provider, throw "Already connected" exception.
        //2. Else if the countryName corresponds to the original country of the user, do nothing.
        //      This means that the user wants to connect to its original country, for which we do not
        //      require a connection. Thus, return the user as it is.
        //3. Else, the user should be subscribed under a serviceProvider having option to connect to the given country.
        //If the connection can not be made (As user does not have a serviceProvider or serviceProvider does not
        //      have given country, throw "Unable to connect" exception.
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId" and
        //      return the updated user. If multiple service providers allow you to connect to the country, use the
        //      service provider having smallest id.

        User user = userRepository2.findById(userId).get();
        if(user.getConnected()) {
            throw new Exception("Already connected");
        }

        if (user.getOriginalCountry().getCountryName().toString().equals(countryName.toUpperCase())) {
            return user;
        }

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        if(serviceProviderList.size()==0) {
            throw new Exception("Unable to connect");
        }

        ServiceProvider serviceProvider = null;
        for(ServiceProvider currentServiceProvider : serviceProviderList) {
            List<Country> countryList = currentServiceProvider.getCountryList();
            for(Country currentCountry : countryList) {
                if(currentCountry.getCountryName().toString().equals(countryName.toUpperCase())) {
                    if(serviceProvider == null || currentServiceProvider.getId() < serviceProvider.getId()) {
                        serviceProvider = currentServiceProvider;
                    }
                }
            }
        }
        if(serviceProvider == null) {
            throw new Exception("Unable to connect1");
        }


        //Making connection
        Connection connection = new Connection();
        connection.setServiceProvider(serviceProvider);
        connection.setUser(user);

        user.setConnected(true);
        user.setMaskedIp(CountryName.valueOf(countryName.toUpperCase()).toCode() + "." + serviceProvider.getId() + "." + userId);
        user.getConnectionList().add(connection);

        serviceProvider.getConnectionList().add(connection);

        //save
        userRepository2.save(user);
        serviceProviderRepository2.save(serviceProvider);

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        //If the given user was not connected to a vpn, throw "Already disconnected" exception.
        //Else, disconnect from vpn, make masked Ip as null, update relevant attributes and return updated user.
        User user = userRepository2.findById(userId).get();

        if(!user.getConnected()) {
            throw new Exception("Already disconnected");
        }
        //disconnecting VPN
        user.setConnected(false);
        user.setMaskedIp(null);

        userRepository2.save(user);

        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        //Establish a connection between sender and receiver users
        //To communicate to the receiver, sender should be in the current country of the receiver.
        //If the receiver is connected to a vpn, his current country is the one he is connected to.
        //If the receiver is not connected to vpn, his current country is his original country.
        //The sender is initially not connected to any vpn. If the sender's original country does
        //      not match receiver's current country, we need to connect the sender to a suitable vpn.
        //      If there are multiple options, connect using the service provider having smallest id
        //If the sender's original country matches receiver's current country, we do not need to do
        //      anything as they can communicate. Return the sender as it is.
        //If communication can not be established due to any reason, throw "Cannot establish communication" exception

        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        //1. check if the sender is in same country as the receiver, if yes return sender
        //      else try to make vpn connection to the receiver's country : if not possible throw exception
        String senderCountry = sender.getOriginalCountry().getCountryName().toString();
        String receiverCountry = receiver.getMaskedIp()==null ? receiver.getOriginalCountry().getCountryName().toString() : receiver.getMaskedIp().substring(0,3);
        if(senderCountry.equals(receiverCountry)) {
            return sender;
        }
        try {
            if(sender.getConnected()) {
                sender = disconnect(senderId);
            }
            sender = connect(senderId, receiverCountry);
            return sender;
        }catch (Exception e) {
//            if(e.getMessage().equals("Already connected") || e.getMessage().equals("Unable to connect")) {
//                throw new Exception("Cannot establish communication");
                throw new Exception(e.getMessage());
//            }
//            return sender;
        }
    }
}
