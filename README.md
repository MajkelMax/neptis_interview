prosty serwer TCP/IP który otworzy Socket i zacznie nasłuchiwać przychodzących danych na wybranym porcie. Po odebraniu przychodzącego komunikatu (klient powinien wysłać id użytkownika czyli daną znajdującą się w kolumnie id tabeli "users") serwer pobierze wszystkie pojazdy i oferty ubezpieczeniowe dla danego użytkownika oraz odeśle je w odpowiedzi

Aby poprawnie uruchomić serwer należy podać własny adres URL bazy danych, oraz ustawić login i hasło. Wartości te znajdują się w TcpServer.java 



## SQL ERD

![alt text](https://github.com/MajkelMax/neptis_interview/blob/master/SQLImage.png?raw=true)

