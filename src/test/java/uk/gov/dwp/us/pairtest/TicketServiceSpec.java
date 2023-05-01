package uk.gov.dwp.us.pairtest;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.verify;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;
public class TicketServiceSpec {
    @Mock
    TicketPaymentService ticketPaymentService;
    SeatReservationService seatReservationService;

    @InjectMocks
    TicketServiceImpl ticketService;

    private long validAccount = 1L;
    private long invalidAccount = 0L;

    private TicketTypeRequest adultRequest = new TicketTypeRequest(ADULT, 2);
    private TicketTypeRequest childRequest = new TicketTypeRequest(CHILD, 3);
    private TicketTypeRequest infantRequest = new TicketTypeRequest(INFANT, 1);
    private TicketTypeRequest bigRequest = new TicketTypeRequest(ADULT, 21);
    private TicketTypeRequest[] validRequests = {adultRequest, childRequest, infantRequest};
    private TicketTypeRequest[] noAdultRequests = {childRequest, infantRequest};
    private TicketTypeRequest[] tooManyTicketsRequests = {bigRequest};

    @Test
    public void purchaseTicketsIfValid() {
        //given
        int expectedCost = 70;
        int expectedSeats = 5;
        // Payment and reservation services work

        //when
        ticketService.purchaseTickets(validAccount, validRequests);

        //then
        // Payment and reservation service methods are called
//        verify(seatReservationService.reserveSeat(validAccount, expectedSeats))
//        verify(ticketPaymentService.makePayment(validAccount, expectedCost))

    }

    @Test
    public void throwExceptionIfInvalidRequests() throws InvalidPurchaseException {
        //given
        // TicketTypeRequest is invalid because no adult tickets

        //when
        ticketService.purchaseTickets(validAccount, noAdultRequests);

        //then
        //InvalidPurchaseException is thrown

        //TODO split test of second case?
        //given TicketTypeRequest is invalid because too many tickets
        //when
        ticketService.purchaseTickets(validAccount, tooManyTicketsRequests);

        //then
        //InvalidPurchaseException is thrown
    }

    @Test
    public void throwExceptionIfInvalidAccountId() throws InvalidPurchaseException {
        //given
        // account number is 'invalid'

        //when
        ticketService.purchaseTickets(invalidAccount, validRequests);

        //then
        //InvalidPurchaseException is thrown
    }



}
