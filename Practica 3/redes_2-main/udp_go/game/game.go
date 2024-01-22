package game

import (
	"fmt"
	"math/rand"
	"os"
	"time"
	"udp_go/utils"

	"github.com/regeda/minesweeper"
)

func main() {
	var rows, cols int
	fmt.Print("Enter [rows cols]: ")
	if _, err := fmt.Fscan(os.Stdin, &rows, &cols); err != nil {
		fmt.Println("failed to read rows & cols:", err)
		return
	}
	rand.Seed(time.Now().UnixNano())
	m := minesweeper.GenerateGrid(rows, cols, 0.3)
	g := minesweeper.New(m)
	utils.PrintGrid(m)
	var (
		cmd  string
		i, j int
	)
	for {
		fmt.Print("Go [x y [f-flag|v-unflag|u-unfold]]: ")
		if _, err := fmt.Fscan(os.Stdin, &j, &i, &cmd); err != nil {
			fmt.Println("failed to read an action:", err)
			continue
		}
		switch cmd {
		case "f":
			m[i][j].Flag(true)
			utils.PrintGrid(m)
		case "v":
			m[i][j].Flag(false)
			utils.PrintGrid(m)
		case "u":
			left, ok := g.Unfold(i, j)
			utils.PrintGrid(m)
			if !ok {
				fmt.Println("GAME OVER")
				return
			}
			if left == 0 {
				fmt.Println("WIN")
				return
			}
			fmt.Println("left cells:", left)
		default:
			fmt.Println("Unknown command:", cmd)
		}
	}
}
