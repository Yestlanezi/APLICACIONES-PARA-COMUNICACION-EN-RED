package main

import (
	conn "client_go/client"
	menu "client_go/client/menu"
)

func main() {
	menu.LocalMenu()
	conn.EndConnection(conn.Cnn)
}
