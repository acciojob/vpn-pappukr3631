package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        //1. add a country under the serviceProvider and return respective service provider
        //2. country name would be a 3-character string out of ind, aus, usa, chi, jpn. Each character
        //      can be in uppercase or lowercase. You should create a new Country object based on the
        //      given country name and add it to the country list of the service provider. Note that
        //      the user attribute of the country in this case would be null.
        //3. In case country name is not amongst the above mentioned strings, throw "Country not found" exception

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


        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        countryName = countryName.toUpperCase();

        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());

        country.setServiceProvider(serviceProvider);
        country.setUser(null);
        serviceProvider.getCountryList().add(country);

        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;
    }
}
