package http

import (
	"fmt"
	"io"
	"net/http"
)

const htmlContent = "<html><body><h1>Hello, World!</h1></body></html>"

func getRoot(w http.ResponseWriter, r *http.Request) {
	fmt.Printf("got / request\n")
	io.WriteString(w, "This is my website!\n")
}
func getHello(w http.ResponseWriter, r *http.Request) {
	fmt.Printf("got /hello request\n")
	io.WriteString(w, "Hello, HTTP!\n")
}

func postHtml(w http.ResponseWriter, r *http.Request) {
	fs := http.FileServer(http.Dir("index"))
	fmt.Println(fs)
	fmt.Fprintf(w, htmlContent)
}

func StartSever() {
	mux := http.NewServeMux()
	mux.HandleFunc("/", getRoot)
	mux.HandleFunc("/healthcheck", getHello)
	mux.HandleFunc("/html", postHtml)
	fmt.Println("starting server!")
	fmt.Println("listening in: http://localhost:8082")
	http.ListenAndServe(":8082", mux)
}
