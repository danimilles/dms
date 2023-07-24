package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.PaymentViewData;
import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.model.view.ServiceViewData;

import java.util.List;

public interface DmsCommunicationFacade {
    List<ClientViewData> getClientTableData();

    void deletePayment(PaymentViewData paymentViewData);

    Long savePayment(PaymentViewData paymentViewData);

    List<SearchTableRow> getSearchTableData(SearchTableRow searchTableRow);

    ClientViewData getClientViewData(Long clientId);

    List<DateViewData> getDateCalendarData();

    Long saveClient(ClientViewData clientViewData);

    void saveClientDog(Long idclient, Long iddog);

    void deleteClientDog(Long idclient, Long iddog);

    void deleteClient(Long id);

    Long saveDog(DogViewData dogViewData);

    void deleteDog(Long id);

    DogViewData getDogViewData(Long dogId);

    Long saveDate(DateViewData dateViewData);

    void deleteDate(DateViewData dateViewData);

    List<ServiceViewData> getServiceTableData();

    ServiceViewData getServiceViewData(Long serviceId);

    void deleteService(ServiceViewData serviceViewData);

    Long saveService(ServiceViewData serviceViewData);

    PaymentViewData getPaymentViewData(Long serviceId);

    List<PaymentViewData> getPaymentTableData();

    DateViewData getDateViewData(Long id);

    List<DogViewData> getDogTableData();
}
