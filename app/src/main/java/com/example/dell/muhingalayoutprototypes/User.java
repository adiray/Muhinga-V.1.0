package com.example.dell.muhingalayoutprototypes;

public class User {

    String email, password, first_name, last_name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public User(String eMail, String passWord, String first_name, String last_name) {
        this.email = eMail;
        this.password = passWord;
        this.first_name = first_name;
        this.last_name = last_name;
    }
}



/*


todo https://icons8.com  ILLUSTRATIONS BY OUCH LINKS

[
	{
		"lastLogin": null,
		"userStatus": "ENABLED",
		"socialAccount": "BACKENDLESS",
		"created": 1554126863555,
		"email": "adiray1@outlook.com",
		"updated": null,
		"objectId": "5B1CB332-745B-45DA-FF94-2DE2CEC28700",
		"last_name": "Raymond",
		"first_name": "Adibaku",
		"ownerId": "5B1CB332-745B-45DA-FF94-2DE2CEC28700",
		"___class": "Users"
	}
]

***********************************************************************************

	{
		"email": "adibakuray@gmail.com",
		"last_name": "Raymond",
		"first_name": "Adibaku",
		"password":"123"
		}

		*********************************************************************************

		{
	"userStatus": "ENABLED",
	"created": 1554127402767,
	"___class": "Users",
	"last_name": "Raymond",
	"ownerId": "6EAFFE12-B424-8C51-FFB0-FE99548CAA00",
	"first_name": "Adibaku",
	"updated": null,
	"email": "adibakuray@gmail.com",
	"objectId": "6EAFFE12-B424-8C51-FFB0-FE99548CAA00"
}


*/