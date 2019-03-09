Imports System.Collections.Generic
Module CodeALaMode

    Public Debug As Boolean = True
    Public Const Dish As String = "DISH"

    Public Class Game
        Public Players As Player() = New Player(1) {}
        Public Dishwasher As Table
        Public Window As Table
        Public Blueberry As Table
        Public IceCream As Table
        Public Tables As New List(Of Table)

        Function getFirstEmptyTable() As Table
            For Each t As Table In Tables
                If t.Item Is Nothing Then Return t
            Next
            Throw New InvalidOperationException
        End Function
        Function getTableAtPosition(p As Position) As Table
            For Each t As Table In Tables
                If t.Position.X = p.X AndAlso t.Position.Y = p.Y Then
                    Return t
                End If
            Next
            Throw New InvalidOperationException
        End Function
    End Class

    Public Class Table
        Public Position As Position
        Public HasFunction As Boolean
        Public Item As Item
    End Class

    Public Class Item
        Public Content As String
        Public HasPlate As Boolean
        Public Sub New(content As String)
            Me.Content = content
            Me.HasPlate = content.Contains(CodeALaMode.Dish)
        End Sub
    End Class

    Public Class Player
        Public Position As Position
        Public Item As Item
        Public Sub New(Position As Position, Item As Item)
            Me.Position = Position
            Me.Item = Item
        End Sub
        Public Sub Update(position As Position, item As Item)
            Me.Position = position
            Me.Item = item
        End Sub
    End Class

    Public Class Position
        Public X, Y As Integer
        Public Sub New(x As Integer, y As Integer)
            Me.X = x
            Me.Y = y
        End Sub

        Public Function Manhattan(p2 As Position) As Integer
            Return Math.Abs(X - p2.X) + Math.Abs(Y - p2.Y)
        End Function

        Public Overrides Function ToString() As String
            Return X & " " & Y
        End Function
    End Class

    Public Function ReadGame() As Game
        Dim game As Game = New Game()
        game.Players(0) = New Player(Nothing, Nothing)
        game.Players(1) = New Player(Nothing, Nothing)

        For i As Integer = 0 To 7 - 1
            Dim kitchenLine As String = ReadLine()
            For x As Integer = 0 To kitchenLine.Length - 1
                Dim t As New Table
                Select Case kitchenLine(x)
                    Case "W"c
                        t.Position = New Position(x, i) : t.HasFunction = True
                        game.Window = t
                    Case "D"c
                        t.Position = New Position(x, i) : t.HasFunction = True
                        game.Dishwasher = t
                    Case "I"c
                        t.Position = New Position(x, i) : t.HasFunction = True
                        game.IceCream = T
                    Case "B"c
                        T.Position = New Position(x, i) : T.HasFunction = True
                        game.Blueberry = T
                    Case "#"c
                        t.Position = New Position(x, i)
                        game.Tables.Add(t)
                End Select
            Next
        Next

        Return game
    End Function

    Private Sub Move(p As Position)
        Console.WriteLine("MOVE " & p.ToString())
    End Sub

    Private Sub Use(p As Position)
        Console.WriteLine("USE " & p.ToString() & "; VB Starter AI")
    End Sub

    Private Function ReadLine() As String
        Dim s As String = Console.ReadLine()
        If Debug Then Console.Error.WriteLine(s)
        Return s
    End Function


    Sub Main()
        Dim inputs As String()

        ' ALL CUSTOMERS INPUT to ignore until Bronze
        Dim numAllCustomers As Integer = CInt(ReadLine())
        For i As Integer = 0 To numAllCustomers - 1
            inputs = ReadLine().Split(" "c)
            Dim customerItem As String = inputs(0) ' the food the customer Is waiting for
            Dim customerAward As Integer = CInt(inputs(1)) ' the number Of points awarded For delivering the food
        Next

        ' KITCHEN INPUT
        Dim Game As Game = ReadGame()

        While True
            Dim turnsRemaining As Integer = CInt(ReadLine())

            ' PLAYERS INPUT
            inputs = ReadLine().Split(" "c)
            Game.Players(0).Update(New Position(CInt(inputs(0)), CInt(inputs(1))), New Item(inputs(2)))
            inputs = ReadLine().Split(" "c)
            Game.Players(1).Update(New Position(CInt(inputs(0)), CInt(inputs(1))), New Item(inputs(2)))

            'Clean other tables
            For Each t As Table In Game.Tables
                t.Item = Nothing
            Next
            Dim numTablesWithItems As Integer = CInt(ReadLine()) ' the number Of tables In the kitchen that currently hold an item
            For i As Integer = 0 To numTablesWithItems - 1
                inputs = ReadLine().Split(" "c)
                Dim position As New Position(CInt(inputs(0)), CInt(inputs(1)))
                Dim Table as Table = Game.getTableAtPosition(position)
                Table.Item = New Item(inputs(2))
            Next

            inputs = ReadLine().Split(" "c)
            Dim ovenContents As String = inputs(0) ' ignore until bronze league
            Dim ovenTimer As Integer = CInt(inputs(1))
            Dim numCustomers As Integer = CInt(ReadLine()) ' the number Of customers currently waiting For food
            For i As Integer = 0 To numCustomers - 1
                inputs = ReadLine().Split(" "c)
                Dim customerItem As String = inputs(0)
                Dim customerAward As Integer = CInt(inputs(1))
            Next

            ' GAME LOGIC
            ' fetch a dish, pick ice cream And drop the dish on an empty table
            Dim myChef As Player = Game.Players(0)
            Dim myChefhasPlate As Boolean = If(myChef.Item IsNot Nothing, myChef.Item.HasPlate, False)
            If (Not myChefhasPlate) Then
                Use(Game.Dishwasher.Position)
            ElseIf (Not myChef.Item.Content.Contains("ICE_CREAM")) Then
                Use(Game.IceCream.Position)
                ' once ready, put it on the first empty table for now
            Else
                Use(Game.getFirstEmptyTable.Position)
            End If
        End While
    End Sub

End Module
