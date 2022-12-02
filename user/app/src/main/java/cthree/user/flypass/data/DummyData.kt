package cthree.user.flypass.data

import cthree.user.flypass.R
import cthree.user.flypass.models.airport.Airport

object DummyData {
    val firstTicketList = arrayListOf(
        Ticket(
            departureTime = "17:30",
            arrivalTime = "18:30",
            aitaArrival = "CGK",
            aitaDeparture = "YIA",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 450000,
            seatClass = "Economy",
            flightCode = "GIA-8783",
        ),
        Ticket(
            departureTime = "19:30",
            arrivalTime = "20:30",
            aitaArrival = "YIA",
            aitaDeparture = "DPS",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-4567",
        ),
        Ticket(
            departureTime = "17:30",
            arrivalTime = "18:30",
            aitaArrival = "CGK",
            aitaDeparture = "YIA",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 450000,
            seatClass = "Economy",
            flightCode = "GIA-8783",
        ),
        Ticket(
            departureTime = "19:30",
            arrivalTime = "20:30",
            aitaArrival = "YIA",
            aitaDeparture = "DPS",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-4567",
        ),
        Ticket(
            departureTime = "17:30",
            arrivalTime = "18:30",
            aitaArrival = "CGK",
            aitaDeparture = "YIA",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 450000,
            seatClass = "Economy",
            flightCode = "GIA-8783",
        ),
        Ticket(
            departureTime = "19:30",
            arrivalTime = "20:30",
            aitaArrival = "YIA",
            aitaDeparture = "DPS",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-4567",
        ),
        Ticket(
            departureTime = "17:30",
            arrivalTime = "18:30",
            aitaArrival = "CGK",
            aitaDeparture = "YIA",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 450000,
            seatClass = "Economy",
            flightCode = "GIA-8783",
        )
    )
    val secondTicketList = arrayListOf(
        Ticket(
            departureTime = "15:30",
            arrivalTime = "17:30",
            aitaArrival = "YIA",
            aitaDeparture = "CGK",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-3493",
        ),
        Ticket(
            departureTime = "07:30",
            arrivalTime = "09:30",
            aitaArrival = "DPS",
            aitaDeparture = "YIA",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 700000,
            seatClass = "Economy",
            flightCode = "GIA-1272",
        ),
        Ticket(
            departureTime = "15:30",
            arrivalTime = "17:30",
            aitaArrival = "YIA",
            aitaDeparture = "CGK",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-3493",
        ),
        Ticket(
            departureTime = "07:30",
            arrivalTime = "09:30",
            aitaArrival = "DPS",
            aitaDeparture = "YIA",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 700000,
            seatClass = "Economy",
            flightCode = "GIA-1272",
        ),
        Ticket(
            departureTime = "15:30",
            arrivalTime = "17:30",
            aitaArrival = "YIA",
            aitaDeparture = "CGK",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-3493",
        ),
        Ticket(
            departureTime = "07:30",
            arrivalTime = "09:30",
            aitaArrival = "DPS",
            aitaDeparture = "YIA",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 700000,
            seatClass = "Economy",
            flightCode = "GIA-1272",
        ),
        Ticket(
            departureTime = "15:30",
            arrivalTime = "17:30",
            aitaArrival = "YIA",
            aitaDeparture = "CGK",
            duration = 4000,
            airplaneName = "Garuda Indonesia",
            price = 500000,
            seatClass = "Economy",
            flightCode = "GIA-3493",
        ),
        Ticket(
            departureTime = "07:30",
            arrivalTime = "09:30",
            aitaArrival = "DPS",
            aitaDeparture = "YIA",
            duration = 3000,
            airplaneName = "Garuda Indonesia",
            price = 700000,
            seatClass = "Economy",
            flightCode = "GIA-1272",
        ),
    )

    val onBoardingItem = listOf(
        OnBoarding(
            R.drawable.ic_on_boarding_1,
            R.string.title_on_boarding_1,
            "Pesan tiketmu kapanpun dan dimanapun kamu berada."
        ),
        OnBoarding(
            R.drawable.ic_on_boarding_1,
            R.string.title_on_boarding_1,
            "Pesan tiketmu kapanpun dan dimanapun kamu berada."
        ),
        OnBoarding(
            R.drawable.ic_on_boarding_1,
            R.string.title_on_boarding_1,
            "Pesan tiketmu kapanpun dan dimanapun kamu berada."
        )
    )

    val highlightTopicItem = listOf(
        HighlightTopic(
            "Highlight 1"
        ),
        HighlightTopic(
            "Highlight 2"
        ),
        HighlightTopic(
            "Highlight 3"
        ),
    )
    val frequentSearch = listOf(
        Airport(
            city = "Icy Bay",
            country = "US",
            iata = "ICY",
            id = 1,
            name = "Icy Bay Airport"
        ),
        Airport(
            city = "Hogatza",
            country = "US",
            iata = "HGZ",
            id = 1,
            name = "Hog River Airport"
        ),
        Airport(
            city = "Yogyakarta",
            country = "Indonesia",
            iata = "YIA",
            id = 1,
            name = "Yogyakarta International Airport"
        ),
        Airport(
            city = "Singapore",
            country = "Singapore",
            iata = "CHG",
            id = 1,
            name = "Changi Airport"
        ),
    )

    val japanAirport  = listOf(
        Airport(
            city = "Tokyo",
            country = "Japan",
            iata = "CHG",
            id = 1,
            name = "Narita Airport"
        ),
        Airport(
            city = "Tokyo",
            country = "Japan",
            iata = "CHG",
            id = 1,
            name = "Narita Airport"
        ),
        Airport(
            city = "Tokyo",
            country = "Japan",
            iata = "CHG",
            id = 1,
            name = "Narita Airport"
        ),
        Airport(
            city = "Tokyo",
            country = "Japan",
            iata = "CHG",
            id = 1,
            name = "Narita Airport"
        ),
    )

    val indoAirport = listOf(
        Airport(
            city = "Bali",
            country = "Indonesia",
            iata = "DPS",
            id = 1,
            name = "Ngurah Rai Airport"
        ),
        Airport(
            city = "Bali",
            country = "Indonesia",
            iata = "DPS",
            id = 1,
            name = "Ngurah Rai Airport"
        ),
        Airport(
            city = "Bali",
            country = "Indonesia",
            iata = "DPS",
            id = 1,
            name = "Ngurah Rai Airport"
        ),
        Airport(
            city = "Bali",
            country = "Indonesia",
            iata = "DPS",
            id = 1,
            name = "Ngurah Rai Airport"
        ),
    )
    val chinaAirport = listOf(
        Airport(
            city = "Beijing",
            country = "China",
            iata = "PEK",
            id = 1,
            name = "Beijing Capital International Airport"
        ),
        Airport(
            city = "Beijing",
            country = "China",
            iata = "PEK",
            id = 1,
            name = "Beijing Capital International Airport"
        ),
        Airport(
            city = "Beijing",
            country = "China",
            iata = "PEK",
            id = 1,
            name = "Beijing Capital International Airport"
        ),
        Airport(
            city = "Beijing",
            country = "China",
            iata = "PEK",
            id = 1,
            name = "Beijing Capital International Airport"
        ),
    )
}