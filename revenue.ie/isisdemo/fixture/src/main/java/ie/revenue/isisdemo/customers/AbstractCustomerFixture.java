package ie.revenue.isisdemo.customers;

import ie.revenue.isisdemo.corresp.CorrespondenceHistory;
import ie.revenue.isisdemo.custprofile.CustomerName;
import ie.revenue.isisdemo.custprofile.CustomerProfile;
import ie.revenue.isisdemo.custprofile.CustomerTitle;
import ie.revenue.isisdemo.custprofile.Language;
import ie.revenue.isisdemo.taxrecord.CustomerTaxCredit;
import ie.revenue.isisdemo.taxrecord.CustomerTaxRecord;
import ie.revenue.isisdemo.taxrecord.CustomerTaxRecords;
import ie.revenue.isisdemo.taxrecord.CustomerTaxYear;
import ie.revenue.isisdemo.taxrecord.TaxCreditEligibility;
import ie.revenue.isisdemo.taxrecord.TaxCreditRate;
import ie.revenue.isisdemo.taxrecord.TaxCreditType;
import ie.revenue.isisdemo.taxrecord.TaxYear;

import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.applib.value.Date;

public abstract class AbstractCustomerFixture extends AbstractFixture {

	protected Customer createCustomer(String ppsn, CustomerTitle customerTitle,
			String firstName, String surname, String mothersBirthSurname,
			String pin, Date dateOfBirth, String phoneNumber,
			String mobileNumber, String emailAddress,
			Language language, int numberOfDependentChildren,
			boolean goPaperFree) {

		// customer, name, credentials
		Customer customer = newTransientInstance(Customer.class);
		customer.setPpsn(ppsn);

		CustomerCredentials credentials = newTransientInstance(CustomerCredentials.class);
		credentials.setMothersMaidenSurname(mothersBirthSurname);
		credentials.setRevenuePin(pin);

		customer.setCredentials(credentials);
		credentials.setCustomer(customer);

		getContainer().persist(customer);

		createCustomerProfile(customer, customerTitle, firstName, surname);
		createCorrespondenceHistory(customer);
		createTaxRecordAndCustomerTaxYears(customer);
		
		return customer;
	}


	private void createCustomerProfile(Customer customer,
			CustomerTitle customerTitle, String firstName, String surname) {
		// profile, name
		CustomerProfile profile = newTransientInstance(CustomerProfile.class);
		profile.setCustomer(customer);

		CustomerName name = newTransientInstance(CustomerName.class);
		profile.setName(name);
		name.setProfile(profile);
		name.setFirstName(firstName);
		name.setSurname(surname);
		name.setTitle(customerTitle);
		
		
		getContainer().persist(profile);
	}

	private void createCorrespondenceHistory(Customer customer) {
		CorrespondenceHistory correspondenceHistory = newTransientInstance(CorrespondenceHistory.class);
		correspondenceHistory.setCustomer(customer);

		getContainer().persist(correspondenceHistory);
	}

	private void createTaxRecordAndCustomerTaxYears(Customer customer) {
		// tax record
		CustomerTaxRecord taxRecord = newTransientInstance(CustomerTaxRecord.class);
		taxRecord.setCustomer(customer);

		// tax years
		List<TaxYear> taxYears = getContainer().allInstances(TaxYear.class);
		for (TaxYear taxYear : taxYears) {
			CustomerTaxYear cty = newTransientInstance(CustomerTaxYear.class);
			cty.setTaxRecord(taxRecord);
			cty.setTaxYear(taxYear);
		}
		
		getContainer().persist(taxRecord);
	}

	protected void addCustomerTaxCredit(Customer customer, int year,
			String taxCreditTypeCode, TaxCreditEligibility eligibility) {
		CustomerTaxRecord taxRecord = customerTaxRecords.taxRecordFor(customer);
		CustomerTaxYear cty = taxRecord.taxYearFor(year);
		
		CustomerTaxCredit ctc = newTransientInstance(CustomerTaxCredit.class);
		ctc.setCustomerTaxYear(cty);
		ctc.setTaxCreditRate(taxCreditRateFor(year, taxCreditTypeCode));
		
		ctc.setEligibility(eligibility);
		getContainer().persist(ctc);
	}

	private TaxCreditRate taxCreditRateFor(int year, String taxCreditTypeCode) {
		TaxCreditType tct = TaxCreditType.lookup(taxCreditTypeCode, getContainer());
		TaxYear ty = TaxYear.lookup(year, getContainer());
		return TaxCreditRate.lookup(ty, tct, getContainer());
	}

	
	// {{ injected: Customers
	private Customers customers;
	public Customers getCustomers() {
		return customers;
	}
	public void setCustomers(final Customers customers) {
		this.customers = customers;
	}
	// }}


	// {{ injected: CustomerTaxRecords
	private CustomerTaxRecords customerTaxRecords;
	public CustomerTaxRecords getCustomerTaxRecords() {
		return customerTaxRecords;
	}
	public void setCustomerTaxRecords(final CustomerTaxRecords taxRecords) {
		this.customerTaxRecords = taxRecords;
	}
	// }}


}