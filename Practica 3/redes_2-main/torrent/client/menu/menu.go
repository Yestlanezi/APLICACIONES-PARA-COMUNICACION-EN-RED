package menu

import "github.com/turret-io/go-menu/menu"

func RemoteMenu() {
	commandOptions := []menu.CommandOption{
		{Command: "ls", Description: "Shows files in dir", Function: Ls},
		{Command: "clear", Description: "Clear screen", Function: Clear},
		// {Command: "mkdir", Description: "Make directory", Function: Mkdir},
		// {Command: "pwd", Description: "Print Working Directory", Function: Pwd},
		// {Command: "cd", Description: "Change Directory", Function: Cd},
		// {Command: "mkf", Description: "Make file", Function: Mkf},
		// {Command: "rmf", Description: "Remove file", Function: Rmf},
		// {Command: "rmdirR", Description: "Remove Dir recursively", Function: RmdirR},
		// {Command: "rename", Description: "Rename file or dir", Function: Rename},
		// {Command: "search", Description: "Search file", Function: Search},
		// {Command: "download", Description: "Downloads file or dir", Function: Download},
		{Command: "search", Description: "Search file or dir", Function: SearchW},
		{Command: "download", Description: "Download file or dir", Function: DownloadW},
	}
	menuOptions := menu.NewMenuOptions("console"+" > ", 0)

	menu := menu.NewMenu(commandOptions, menuOptions)
	menu.Start()
}
