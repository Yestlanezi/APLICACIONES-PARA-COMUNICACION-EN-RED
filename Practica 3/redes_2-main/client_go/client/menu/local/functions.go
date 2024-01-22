package local

import (
	conn "client_go/client"
	utils "client_go/client/utils"
	"fmt"
	"io"
	"io/ioutil"
	"os"
	"os/exec"
	"syscall"
)

func Ls(args ...string) (err error) {
	var dir string
	files, err := ioutil.ReadDir(PwdR())
	if err != nil {
		fmt.Println(err)
	}

	for _, file := range files {
		if file.IsDir() == true {
			dir = "dir"
		} else {
			dir = ""
		}
		fmt.Println(file.Name(), dir)
	}
	return nil
}

func Clear(args ...string) error {
	c := exec.Command("clear")
	c.Stdout = os.Stdout
	c.Run()
	return nil
}

func Mkdir(args ...string) error {
	if len(args) < 1 {
		fmt.Println("Error: Missing dir name")
	} else {
		if err := os.MkdirAll(PwdR()+"/"+args[0], os.ModePerm); err != nil {
			fmt.Println(err)
		}
	}
	return nil
}

func Pwd(args ...string) (err error) {
	CurrentWD, err := syscall.Getwd()
	fmt.Println(CurrentWD)
	return nil
}

func PwdR() string {
	CurrentWD, _ := syscall.Getwd()
	return CurrentWD
}

func Cd(args ...string) error {
	if len(args) < 1 {
		fmt.Println("No such file or directory")
	} else {
		syscall.Chdir(PwdR() + "/" + args[0])
		fmt.Println(PwdR())
	}

	return nil
}

func Mkf(args ...string) (err error) {
	f, err := os.Create(args[0])
	if err != nil {
		fmt.Println(err)
	}
	defer f.Close()
	fmt.Println(f.Name())
	return nil
}

func Rmf(args ...string) (e error) {
	if len(args) < 1 {
		fmt.Println("No such file")
	} else {
		e = os.Remove(args[0])
		if e != nil {
			fmt.Println(e)
		}
	}
	return nil
}

func RmdirR(args ...string) (err error) {
	if len(args) < 1 {
		fmt.Println("No such file or directory")
	} else {
		err = os.RemoveAll(args[0])
		if err != nil {
			fmt.Println(err)
		}
	}
	return nil
}

func Rename(args ...string) (err error) {
	err = os.Rename(args[0], args[1])
	if err != nil {
		fmt.Println(err)
	}
	return nil
}

func Upf(args ...string) (err error) {
	if len(args) < 1 {
		fmt.Println("Error: missing path of filename")
		return
	}
	file, err := os.Open(args[0])
	if err != nil {
		fmt.Println("Failed reading dir: ", err)
		return
	}
	defer file.Close()
	err = conn.GetInstance().Stor(args[1]+args[0], file)
	if err != nil {
		fmt.Println("Error sending file: ", err)
	}
	return
}

func Dlf(args ...string) (err error) {
	res, err := conn.GetInstance().Retr(args[0])
	if err != nil {
		fmt.Println(err)
	}

	defer res.Close()

	outFile, err := os.Create(args[0])
	if err != nil {
		fmt.Println(err)
	}

	defer outFile.Close()

	_, err = io.Copy(outFile, res)
	if err != nil {
		fmt.Println(err)
	}
	return
}

func Uld(args ...string) (err error) {
	//name.zip path source
	utils.Zip(args[2], args[0])
	Upf(args...)
	Rmf(args...)
	return
}

func Dld(args ...string) (err error) {
	utils.UnZip(args[0], args[1])
	return
}
