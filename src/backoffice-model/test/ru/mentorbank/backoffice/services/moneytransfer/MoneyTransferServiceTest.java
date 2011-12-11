package ru.mentorbank.backoffice.services.moneytransfer;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.mentorbank.backoffice.dao.OperationDao;
import ru.mentorbank.backoffice.dao.exception.OperationDaoException;
import ru.mentorbank.backoffice.dao.stub.OperationDaoStub;
import ru.mentorbank.backoffice.model.Operation;
import ru.mentorbank.backoffice.model.stoplist.JuridicalStopListRequest;
import ru.mentorbank.backoffice.model.stoplist.PhysicalStopListRequest;
import ru.mentorbank.backoffice.model.stoplist.StopListInfo;
import ru.mentorbank.backoffice.model.stoplist.StopListStatus;
import ru.mentorbank.backoffice.model.transfer.AccountInfo;
import ru.mentorbank.backoffice.model.transfer.JuridicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.PhysicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.TransferRequest;
import ru.mentorbank.backoffice.services.accounts.AccountService;
import ru.mentorbank.backoffice.services.accounts.AccountServiceBean;
import ru.mentorbank.backoffice.services.moneytransfer.exceptions.TransferException;
import ru.mentorbank.backoffice.services.stoplist.StopListService;
import ru.mentorbank.backoffice.services.stoplist.StopListServiceStub;
import ru.mentorbank.backoffice.test.AbstractSpringTest;

public class MoneyTransferServiceTest extends AbstractSpringTest {

	@Autowired
	private MoneyTransferService moneyTransferService;
	private JuridicalAccountInfo srcAcc;
	private PhysicalAccountInfo dstAcc;
	private TransferRequest transferRequest;
	private StopListInfo stopListInfo;

	@Before
	public void setUp() {
		srcAcc = new JuridicalAccountInfo();
		dstAcc = new PhysicalAccountInfo();
		transferRequest = new TransferRequest();
		stopListInfo = new StopListInfo();
	}

	@Test
	public void transfer() throws TransferException, OperationDaoException {
		// TODO: (done) Необходимо протестировать, что для хорошего перевода всё
		// работает и вызываются все необходимые методы сервисов
		// Далее следует закоментированная закотовка
		OperationDao mockedOperationDao = mock(OperationDaoStub.class);
		StopListService mockedStopListService = mock(StopListServiceStub.class);
		AccountService mockedAccountService = mock(AccountServiceBean.class);

		srcAcc.setInn(StopListServiceStub.INN_FOR_OK_STATUS);
		dstAcc.setAccountNumber("123123123");

		MoneyTransferServiceBean moneyTransferService = new MoneyTransferServiceBean();
		moneyTransferService.setAccountService(mockedAccountService);
		moneyTransferService.setStopListService(mockedStopListService);
		moneyTransferService.setOperationDao(mockedOperationDao);

		transferRequest.setSrcAccount(srcAcc);
		transferRequest.setDstAccount(dstAcc);

		when(mockedAccountService.verifyBalance(any(AccountInfo.class))).thenReturn(true);

		stopListInfo.setStatus(StopListStatus.OK);
		when(mockedStopListService.getJuridicalStopListInfo(any(JuridicalStopListRequest.class)))
			.thenReturn(stopListInfo);
		when(mockedStopListService.getPhysicalStopListInfo(any(PhysicalStopListRequest.class)))
			.thenReturn(stopListInfo);

		moneyTransferService.transfer(transferRequest);

		verify(mockedStopListService).getJuridicalStopListInfo(any(JuridicalStopListRequest.class));
		verify(mockedAccountService).verifyBalance(srcAcc);

		verify(mockedOperationDao).saveOperation(any(Operation.class));
	}
}
