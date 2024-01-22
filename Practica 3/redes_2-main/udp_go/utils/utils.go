package utils

import (
	"bytes"
	"encoding/gob"
	"fmt"

	"github.com/regeda/minesweeper"
)

func Encode(input any, buffer *bytes.Buffer) (err error) {
	var encoder *gob.Encoder
	encoder = gob.NewEncoder(buffer)
	err = encoder.Encode(input)
	return
}

func Decode(input any, buffer_input []byte) (err error) {
	var decoder *gob.Decoder
	var buffer bytes.Buffer
	buffer = *bytes.NewBuffer(buffer_input)
	decoder = gob.NewDecoder(&buffer)
	err = decoder.Decode(input)
	return
}

func PrintGrid(m minesweeper.Grid) {
	for _, r := range m {
		for _, c := range r {
			if c.Unfolded() {
				if c.IsBomb() {
					fmt.Print("x")
				} else {
					fmt.Print(c.Bombs())
				}
			} else if c.Flagged() {
				fmt.Print("F")
			} else {
				fmt.Print("#")
			}
			fmt.Print(" ")
		}
		fmt.Printf("\n")
	}
}
