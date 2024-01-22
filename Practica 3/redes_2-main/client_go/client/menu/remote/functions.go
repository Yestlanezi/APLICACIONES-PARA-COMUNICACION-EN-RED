package remote

import (
	"bytes"
	"fmt"
	"os"
	"strings"

	conn "client_go/client"

	"github.com/jlaffaye/ftp"
)

var HOST = os.Getenv("HOST")
var PORT = os.Getenv("PORT")
var TYPE = os.Getenv("TYPE")

func Mkf(args ...string) (err error) {
	var s, cmd string
	var text []string
	if len(args) == 0 {
		fmt.Println("Error: Empty file name")
		return
	} else {
		cmd = args[len(args)-1]
		text = args[:len(args)-1]

		s = strings.Join(text, " ")
		data := bytes.NewBufferString(s)
		err = conn.GetInstance().Stor(cmd, data)

		if err != nil {
			fmt.Println(err)
		}
	}
	return
}

func Ls(args ...string) (err error) {
	entry, err := conn.GetInstance().List(PwdR())
	if err != nil {
		fmt.Println("List error: ", err)
		return
	}
	for _, v := range entry {
		if v.Type == ftp.EntryTypeFolder {
			fmt.Println(v.Name, "dir")
		} else {
			fmt.Println(v.Name)
		}
	}
	return
}

func Pwd(args ...string) (err error) {
	fmt.Println(PwdR())
	return
}

func PwdR() (path string) {
	path, err := conn.GetInstance().CurrentDir()
	if err != nil {
		fmt.Println("pwd error: ", err)
	}
	return
}

func Mkdir(args ...string) (err error) {
	conn.GetInstance().MakeDir(args[0])
	if err != nil {
		fmt.Println("Error in connection: ", err)
	}
	return
}

func Cd(args ...string) (err error) {
	err = conn.GetInstance().ChangeDir(args[0])
	if err != nil {
		fmt.Println("No such path")
	}
	return
}

func Rmf(args ...string) (err error) {
	err = conn.GetInstance().Delete(args[0])
	if err != nil {
		fmt.Println("No such file")
	}
	return
}

func RmdirR(args ...string) (err error) {
	err = conn.GetInstance().RemoveDirRecur(args[0])
	if err != nil {
		fmt.Println("No such directory")
	}
	return
}

func Rename(args ...string) (err error) {
	err = conn.GetInstance().Rename(args[0], args[1])
	if err != nil {
		fmt.Println("No such file or directory")
	}
	return
}
