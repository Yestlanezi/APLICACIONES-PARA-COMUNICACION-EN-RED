package conn

import (
	"fmt"
	"sync"
	"time"

	"github.com/jlaffaye/ftp"
)

var lock = &sync.Mutex{}

type single struct {
}

func Connection(host string) (cnn *ftp.ServerConn) {
	cnn, err := ftp.Dial(host, ftp.DialWithTimeout(5*time.Second))
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

func GetInstance(host string) *ftp.ServerConn {
	// var err error
	if Cnn == nil {
		lock.Lock()
		defer lock.Unlock()
		if Cnn == nil {
			Cnn = Connection(host)
			// if err != nil {
			// 	fmt.Println("Error in connection: ", err)
			// }
		}
	}

	return Cnn
}
