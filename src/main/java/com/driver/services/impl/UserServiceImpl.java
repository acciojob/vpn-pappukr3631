package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        //1. create a user of given country. The originalIp of the user should be "countryCode.userId"
        //      and return the user. Note that right now user is not connected and thus connected would
        //      be false and maskedIp would be null
        //2. Note that the userId is created automatically by the repository layer

        // Checking countryName input is valid or not
        boolean check = false;
        for(CountryName countryName1 : CountryName.values())
        {
            if(countryName1.toString().equals(countryName.toUpperCase())){
                check = true;
            }
        }
        if (!check) {
            throw new Exception("Country not found");
        }

        //Country obj
        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryName.toUpperCase()));
        country.setCode(CountryName.valueOf(countryName.toUpperCase()).toCode());
        country.setServiceProvider(null);

        //User obj
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setMaskedIp(null); // optional
        user.setOriginalCountry(country);
        user.setConnected(false);//optional
        country.setUser(user);
//        System.out.println(user.getId());
        userRepository3.save(user);
        user.setOriginalIp(country.getCode() + "." + user.getId());
        userRepository3.save(user);
//        System.out.println(user.getId());
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        serviceProvider.getUsers().add(user);
        user.getServiceProviderList().add(serviceProvider);

        userRepository3.save(user);
        serviceProviderRepository3.save(serviceProvider);
        return user;
    }
}
