package menu

import (
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"

	"client/conn"

	"github.com/jlaffaye/ftp"
)

var HOST = os.Getenv("HOST")
var PORT = os.Getenv("PORT")
var TYPE = os.Getenv("TYPE")

type Server struct {
	Host     string
	ServerId int
}

var servers = []Server{{Host: "localhost:2021", ServerId: 1}, {Host: "localhost:2022", ServerId: 2}, {Host: "localhost:2023", ServerId: 3}}

func Clear(args ...string) error {
	c := exec.Command("clear")
	c.Stdout = os.Stdout
	c.Run()
	return nil
}

// func Search(args ...string) (err error) {
// 	if len(args) > 0 {
// 		file, id, err := SearchR(args[0])
// 		if err != nil {
// 			fmt.Println("Error searching: ", err)
// 		}
// 		if file != nil {
// 			if file.Type == ftp.EntryTypeFolder {
// 				fmt.Println(file.Name, file.Size, "MB ", "serverId:", id, " dir")
// 			} else {
// 				fmt.Println(file.Name, file.Size, "MB ", "serverId:", id)
// 			}
// 		} else {
// 			fmt.Println("No such file or directory")
// 		}
// 	} else {
// 		fmt.Println("No such file or directory")
// 	}
// 	return
// }

// func SearchR(name string) (file *ftp.Entry, id int, err error) {
// 	var entry []*ftp.Entry
// 	for _, v := range servers {
// 		wd := PwdR(v.Host)
// 		cnn := conn.Connection(v.Host)
// 		entry, err = cnn.List(wd)
// 		if err != nil {
// 			fmt.Println("List error: ", err)
// 			return
// 		}
// 		for _, k := range entry {
// 			if k.Name == name {
// 				file = k
// 				id = v.ServerId
// 			}
// 		}
// 		conn.EndConnection(cnn)
// 	}
// 	return
// }

// func Download(args ...string) (err error) {
// 	var file *ftp.Entry
// 	var id int
// 	var path string
// 	if len(args) > 0 {
// 		file, id, err = SearchR(args[0])
// 		if err != nil {
// 			fmt.Println("Error searching: ", err)
// 		}
// 		if file != nil {
// 			cnn := conn.Connection(servers[id-1].Host)
// 			path, err = cnn.CurrentDir()
// 			if err != nil {
// 				fmt.Println("pwd error: ", err)
// 				return
// 			}
// 			fmt.Println("path: ", path)
// 			walker := cnn.Walk(path)
// 			for walker.Next() {
// 				if walker.Err() != nil {
// 					fmt.Println("Error al iterar:", walker.Err())
// 					return
// 				}
// 				fileInfo := walker.Stat()
// 				fmt.Println("Path: ", walker.Path())
// 				fmt.Println("fileInfo: ", fileInfo)
// 			}
// 		} else {
// 			fmt.Println("No such file or directory")
// 		}
// 	} else {
// 		fmt.Println("No such file or directory")
// 	}
// 	return
// }

func SearchW(args ...string) (err error) {
	if len(args) < 1 {
		fmt.Println("No such file or directory")
	} else {
		var name string
		for _, v := range servers {
			cnn := conn.Connection(v.Host)
			walker := cnn.Walk("/")
			for walker.Next() {
				if walker.Err() != nil {
					fmt.Println("Error al iterar:", walker.Err())
					return
				}
				fileInfo := walker.Stat()
				// fmt.Println("Path: ", walker.Path())
				// fmt.Println("fileInfo: ", fileInfo)
				if fileInfo.Name == args[0] {
					name = fileInfo.Name
					// fmt.Println("Path: ", walker.Path())
					fmt.Println(fileInfo.Name, fileInfo.Size, "MB ", "serverId:", v.ServerId, fileInfo.Type)
					return
				}
			}
			conn.EndConnection(cnn)
		}
		if name == "" {
			fmt.Println("No such file or directory")
		}
	}
	return
}

func DownloadW(args ...string) (err error) {
	if len(args) < 1 {
		fmt.Println("No such file or directory")
	} else {
		var name string
		for _, v := range servers {
			var resp *ftp.Response
			cnn := conn.Connection(v.Host)
			walker := cnn.Walk("/")
			for walker.Next() {
				if walker.Err() != nil {
					fmt.Println("Error al iterar:", walker.Err())
					return
				}
				fileInfo := walker.Stat()
				// fmt.Println("Path: ", walker.Path())
				// fmt.Println("fileInfo: ", fileInfo)
				if fileInfo.Name == args[0] {
					name = fileInfo.Name
					// fmt.Println("name: ", name)
					if fileInfo.Type == ftp.EntryTypeFile {
						resp, err = cnn.Retr(walker.Path())
						if err != nil {
							fmt.Println(err)
						}
						defer resp.Close()
						path := filepath.Join("archivos", walker.Path())
						// fmt.Println("path: ", path)
						outFile, err := os.Create(path)
						if err != nil {
							path := filepath.Join("archivos", name)
							// fmt.Println("inner path: ", path)
							outFile, err = os.Create(path)
							if err != nil {
								fmt.Println("outfile err: ", err)
							}
						}
						defer outFile.Close()
						_, err = io.Copy(outFile, resp)
						if err != nil {
							fmt.Println("copy err: ", err)
						}
					} else if fileInfo.Type == ftp.EntryTypeFolder {
						path := filepath.Join("archivos", walker.Path())
						err := os.MkdirAll(path, 0755)
						if err != nil {
							fmt.Println("Error al crear el directorio:", err)
							continue
						}
						SubWalk(*walker, cnn, name)
					}
					return
				}
			}
			conn.EndConnection(cnn)
		}
		if name == "" {
			fmt.Println("No such file or directory")
		}
	}
	return
}

func SubWalk(walker ftp.Walker, cnn *ftp.ServerConn, name string) {
	for walker.Next() {
		fileInfo := walker.Stat()
		// fmt.Println("Path: ", walker.Path())
		// fmt.Println("fileInfo: ", fileInfo)
		name = fileInfo.Name
		// fmt.Println("name: ", name)
		if fileInfo.Type == ftp.EntryTypeFile {
			resp, err := cnn.Retr(walker.Path())
			if err != nil {
				fmt.Println(err)
			}
			defer resp.Close()
			path := filepath.Join("archivos", walker.Path())
			outFile, err := os.Create(path)
			if err != nil {
				fmt.Println("outfile err: ", err)
			}
			defer outFile.Close()
			_, err = io.Copy(outFile, resp)
			if err != nil {
				fmt.Println("copy err: ", err)
			}
		} else if fileInfo.Type == ftp.EntryTypeFolder {
			path := filepath.Join("archivos", walker.Path())
			err := os.MkdirAll(path, 0755)
			if err != nil {
				fmt.Println("Error al crear el directorio:", err)
			}
			SubWalk(walker, cnn, name)
		}
		return
	}
}

func Ls(args ...string) (err error) {
	var id int
	if len(args) < 1 {
		for _, v := range servers {
			cnn := conn.Connection(v.Host)
			walker := cnn.Walk("/")
			for walker.Next() {
				if walker.Err() != nil {
					fmt.Println("Error al iterar:", walker.Err())
					return
				}
				fileInfo := walker.Stat()
				fmt.Println(fileInfo.Name, fileInfo.Size, "MB ", "serverId:", v.ServerId, fileInfo.Type)
			}
		}
	} else {
		id, err = strconv.Atoi(args[0])
		cnn := conn.Connection(servers[id-1].Host)
		walker := cnn.Walk("/")
		for walker.Next() {
			if walker.Err() != nil {
				fmt.Println("Error al iterar:", walker.Err())
				return
			}
			fileInfo := walker.Stat()
			fmt.Println(fileInfo.Name, fileInfo.Size, "MB ", "serverId:", id, fileInfo.Type)
		}
	}
	return
}

// func Mkf(args ...string) (err error) {
// 	var s, cmd string
// 	var text []string
// 	if len(args) == 0 {
// 		fmt.Println("Error: Empty file name")
// 		return
// 	} else {
// 		cmd = args[len(args)-1]
// 		text = args[:len(args)-1]

// 		s = strings.Join(text, " ")
// 		data := bytes.NewBufferString(s)
// 		err = conn.GetInstance().Stor(cmd, data)

// 		if err != nil {
// 			fmt.Println(err)
// 		}
// 	}
// 	return
// }

// func Ls(args ...string) (err error) {
// 	entry, err := conn.GetInstance().List(PwdR())
// 	if err != nil {
// 		fmt.Println("List error: ", err)
// 		return
// 	}
// 	for _, v := range entry {
// 		if v.Type == ftp.EntryTypeFolder {
// 			fmt.Println(v.Name, "dir")
// 		} else {
// 			fmt.Println(v.Name)
// 		}
// 	}
// 	return
// }

// func Pwd(args ...string) (err error) {
// 	fmt.Println(PwdR())
// 	return
// }

func PwdR(host string) (path string) {
	cnn := conn.Connection(host)
	path, err := cnn.CurrentDir()
	if err != nil {
		fmt.Println("pwd error: ", err)
	}
	conn.EndConnection(cnn)
	return
}

// func Mkdir(args ...string) (err error) {
// 	conn.GetInstance().MakeDir(args[0])
// 	if err != nil {
// 		fmt.Println("Error in connection: ", err)
// 	}
// 	return
// }

// func Cd(args ...string) (err error) {
// 	err = conn.GetInstance().ChangeDir(args[0])
// 	if err != nil {
// 		fmt.Println("No such path")
// 	}
// 	return
// }

// func Rmf(args ...string) (err error) {
// 	err = conn.GetInstance().Delete(args[0])
// 	if err != nil {
// 		fmt.Println("No such file")
// 	}
// 	return
// }

// func RmdirR(args ...string) (err error) {
// 	err = conn.GetInstance().RemoveDirRecur(args[0])
// 	if err != nil {
// 		fmt.Println("No such directory")
// 	}
// 	return
// }

// func Rename(args ...string) (err error) {
// 	err = conn.GetInstance().Rename(args[0], args[1])
// 	if err != nil {
// 		fmt.Println("No such file or directory")
// 	}
// 	return
// }
