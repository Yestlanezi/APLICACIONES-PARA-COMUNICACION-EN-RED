package client

import (
	"fmt"
	"sync"
	"time"

	"github.com/jlaffaye/ftp"
)

var lock = &sync.Mutex{}

type single struct {
}

func connection() (cnn *ftp.ServerConn, err error) {
	cnn, err = ftp.Dial("localhost:2121", ftp.DialWithTimeout(5*time.Second))
	if err != nil {
		fmt.Println(err)
	}

	err = cnn.Login("admin", "admin")
	if err != nil {
		fmt.Println(err)
	}
	return
}

func EndConnection(cnn *ftp.ServerConn) {
	if err := cnn.Quit(); err != nil {
		fmt.Println(err)
	}
	return
}

var Cnn *ftp.ServerConn

func GetInstance() *ftp.ServerConn {
	var err error
	if Cnn == nil {
		lock.Lock()
		defer lock.Unlock()
		if Cnn == nil {
			// fmt.Println("Connected to server.")
			Cnn, err = connection()
			if err != nil {
				fmt.Println("Error in connection: ", err)
			}
			// fmt.Println("Reusing connection")
		}
		// fmt.Println("Reusing connection")
	}

	return Cnn
}
