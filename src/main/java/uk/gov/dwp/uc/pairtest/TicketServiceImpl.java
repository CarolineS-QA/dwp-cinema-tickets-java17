package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    // would use dependency injection
    private SeatReservationService seatReservationService;
    private TicketPaymentService ticketPaymentService;
    // could pull from config
    private int adultTicketCost = 20;
    private int childTicketCost = 10;
    private int infantTicketCost = 0;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId > 0L) {
            if(validTicketRequests(ticketTypeRequests)) {
                // I feel it is a bad assumption to assume external services never fail, but t'is the task
                seatReservationService.reserveSeat(accountId, calculateNumberOfSeats(ticketTypeRequests));
                ticketPaymentService.makePayment(accountId, calculatePayment(ticketTypeRequests));
            } else {
                // I would validate the request before reaching this method rather than throw this exception here
                throw new InvalidPurchaseException();
            }
        } else {
            throw new InvalidPurchaseException();//invalid accountId, consider custom auth exception - but ideally handle separately
        }
    }

    /** Returns true if requests meet the following:
     * - Max 20 tickets
     * - At least 1 adult ticket
     **/
    private boolean validTicketRequests(TicketTypeRequest[] ticketTypeRequests) {
        boolean atLeastOneAdult = false;
        boolean lessThan20Tickets = true;

        int totalTickets = 0;
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
            int tickets = ticketTypeRequest.getNoOfTickets();
            if (ticketType == TicketTypeRequest.Type.ADULT) {
                atLeastOneAdult = true;
            }
            totalTickets += tickets;
        }

        if(totalTickets > 20){
            lessThan20Tickets = false;
        }

        return atLeastOneAdult && lessThan20Tickets;
    }

    private int calculatePayment(TicketTypeRequest[] ticketTypeRequests) {
        int totalCost = 0;
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
            int tickets = ticketTypeRequest.getNoOfTickets();
            switch (ticketType) {
                case ADULT:
                    totalCost += tickets * adultTicketCost;
                case CHILD:
                    totalCost += tickets * childTicketCost;
                case INFANT:
                    totalCost += tickets * infantTicketCost;
            }
        }
        return totalCost;
    }

    private int calculateNumberOfSeats(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
            int tickets = ticketTypeRequest.getNoOfTickets();
            if (ticketType.equals(TicketTypeRequest.Type.CHILD) || ticketType.equals(TicketTypeRequest.Type.ADULT)) {
                totalSeats += tickets;
            }
        }
        return totalSeats;
    }

}

