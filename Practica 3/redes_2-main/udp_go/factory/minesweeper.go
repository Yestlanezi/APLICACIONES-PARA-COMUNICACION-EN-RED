package factory

type Minesweeper struct {
	Game
}

func NewMinesweeper(data GameData) IGame { //cambiar inicializacion de los datos
	return &Minesweeper{
		Game: Game{
			Data: GameData{
				Rows: data.Rows,
				Cols: data.Cols,
			},
		},
	}
}
