package menu

import (
	conn "client_go/client"
	local "client_go/client/menu/local"
	remote "client_go/client/menu/remote"

	"github.com/turret-io/go-menu/menu"
)

var cwd string

func LocalMenu() {
	commandOptions := []menu.CommandOption{
		{Command: "ls", Description: "Shows files in dir", Function: local.Ls},
		{Command: "clear", Description: "Clear screen", Function: local.Clear},
		{Command: "mkdir", Description: "Make directory", Function: local.Mkdir},
		{Command: "pwd", Description: "Print Working Directory", Function: local.Pwd},
		{Command: "cd", Description: "Change Directory", Function: local.Cd},
		{Command: "mkf", Description: "Make file", Function: local.Mkf},
		{Command: "rmf", Description: "Remove file", Function: local.Rmf},
		{Command: "rmdirR", Description: "Remove Dir recursively", Function: local.RmdirR},
		{Command: "rename", Description: "Rename file or dir", Function: local.Rename},
		{Command: "remote", Description: "Connects to a remote server", Function: Remote},
		{Command: "upf", Description: "Uploads file to server", Function: local.Upf},
		{Command: "dlf", Description: "Downloads file from server", Function: local.Dlf},
		{Command: "uld", Description: "Uploads dir and it's contents", Function: local.Uld},
		{Command: "dld", Description: "Downloads dir and it's contents", Function: local.Dld},
	}
	menuOptions := menu.NewMenuOptions("local"+" > ", 0)

	menu := menu.NewMenu(commandOptions, menuOptions)
	menu.Start()
}

func RemoteMenu() {
	commandOptions := []menu.CommandOption{
		{Command: "ls", Description: "Shows files in dir", Function: remote.Ls},
		{Command: "clear", Description: "Clear screen", Function: local.Clear},
		{Command: "mkdir", Description: "Make directory", Function: remote.Mkdir},
		{Command: "pwd", Description: "Print Working Directory", Function: remote.Pwd},
		{Command: "cd", Description: "Change Directory", Function: remote.Cd},
		{Command: "mkf", Description: "Make file", Function: remote.Mkf},
		{Command: "rmf", Description: "Remove file", Function: remote.Rmf},
		{Command: "rmdirR", Description: "Remove Dir recursively", Function: remote.RmdirR},
		{Command: "rename", Description: "Rename file or dir", Function: remote.Rename},
	}
	menuOptions := menu.NewMenuOptions("remote"+" > ", 0)

	menu := menu.NewMenu(commandOptions, menuOptions)
	menu.Start()
}

func Remote(args ...string) (err error) {
	local.Clear()
	conn.GetInstance()
	RemoteMenu()
	return nil
}
