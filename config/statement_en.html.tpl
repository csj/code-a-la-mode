<!-- LEAGUES level1 level2 level3 level4 level5 -->
<div class="statement-body">
  <!-- BEGIN level1 level2 level3 level4 -->
  <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 20px;
        margin-right: 15px;
        margin-left: 15px;
        margin-bottom: 10px;
        text-align: left;">
    <div style="text-align: center; margin-bottom: 6px">
      <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
    </div>
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      <!-- BEGIN level1 -->
      This is a <b>league based</b> challenge.
      <!-- END -->
      <!-- BEGIN level2 -->
      Welcome to the Wood2 league!
      <!-- END -->
      <!-- BEGIN level3 -->
      Welcome to the Wood1 league!
      <!-- END -->
      <!-- BEGIN level4 -->
      Welcome to the Bronze league!
      <!-- END -->
    </p>
    <span class="statement-league-alert-content">
      <!-- BEGIN level1 -->
      Wood leagues should be considered as a tutorial which lets players discover the different rules of the game. <br/>
      In Bronze league, all rules will be unlocked and the real challenge will begin. <br/> <br/>
      <!-- END -->
      <!-- BEGIN level2 -->
      In Wood 2, customers can order a more complex dessert: chopped strawberries. Strawberries need to be chopped at the chopping board. <br/> <br/>
      <!-- END -->
      <!-- BEGIN level3 -->
      In Wood 1, customers can order a more complex dessert: croissants. Dough is cooked into croissants at the oven. <br/> <br/>
      <!-- END -->
      <!-- BEGIN level4 -->
      In Bronze, customers can order an even more complex dessert: blueberry tart. Dough needs to be chopped at the chopping board. Then, blueberries should be added to have a raw tart. The raw tart needs then to be cooked into a blueberry tart at the oven.
      <!-- END -->
      <!-- BEGIN level1 level2 level3 -->
      Starter AIs are available in the <a target="_blank" href="https://github.com/csj/code-a-la-mode/tree/master/src/test/starterkit">Starter Kit</a>. They can help you get started with coding your own bot.
      <!-- END -->
    </span>
  </div>
  <!-- END -->

  <!-- GOAL -->
  <div class="statement-section statement-goal">
    <h2>
      <span class="icon icon-goal">&nbsp;</span>
      <span>The Goal</span>
    </h2>
    <div class="statement-goal-content">
      Control a chef and prepare food for customers as quickly as possible to earn more points than the other players.
    </div>
  </div>

  <!-- RULES -->
  <div class="statement-section statement-rules">
    <h2>
      <span class="icon icon-rules">&nbsp;</span>
      <span>The Game</span>
    </h2>
    <div class="statement-rules-content">
      <p>
        This is a three-player game played on a grid <const>11</const> cells wide and <const>7</const> cells high. A match is played in 3 rounds, each round with only 2 of the players.
      </p>
      <ul style="padding-top:0; padding-bottom:0">
        <li><b>Round 1:</b> player A with player B.</li>
        <li><b>Round 2:</b> player C with player A.</li>
        <li><b>Round 3:</b> player B with player C.</li>
      </ul>
      <p>
        Each player will thus play two rounds in each match. A player's total points is the sum of both rounds' points.
      </p>
      <p>
        Each round lasts for <const>200</const> turns and is played with the same <b>kitchen</b> and <b>customers</b>.
      </p>
      <br>
      <p>
        <b>A round</b>
      </p>
      <p>
        Each player controls a chef who moves around the kitchen and prepares food for customers.
      </p>
      <p>
        Both players play collaboratively, and perform their actions one after the other. Each player will have 100 turns to act per round.
       </p>
      <br>
       <p>
        <b>The kitchen</b>
      </p>
        The kitchen contains:
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Floor cells, on which the chefs can move (<const>.</const>, <const>0</const>, <const>1</const>).</li>
          <li>Empty tables (<const>#</const>).</li>
          <li>A dishwasher (<const>D</const>).</li>
          <li>A customer window represented by a bell (<const>W</const>).</li>
        </ul>
      <!-- BEGIN level1 -->
        It also contains different food crates that dispense:
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Blueberries (<const>B</const>).</li>
          <li>Ice cream (<const>I</const>).</li>
        </ul>
      <!-- END -->
      <!-- BEGIN level2 -->
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
          It also contains different food crates that dispense:
          <ul style="padding-top:0; padding-bottom: 0;">
            <li>Blueberries (<const>B</const>).</li>
            <li>Ice cream (<const>I</const>).</li>
            <li>Strawberries (<const>S</const>).</li>
          </ul>
        <p>
          It also contains a new appliance: a chopping board (<const>C</const>).
        </p>
      </div>
      <!-- END -->
      <!-- BEGIN level3 -->
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
          It also contains different food crates that dispense:
          <ul style="padding-top:0; padding-bottom: 0;">
            <li>Blueberries (<const>B</const>).</li>
            <li>Ice cream (<const>I</const>).</li>
            <li>Strawberries (<const>S</const>).</li>
            <li>Dough (<const>H</const>).</li>
          </ul>

          It also contains two extra appliances: 
          <ul style="padding-top:0; padding-bottom: 0;">
            <li>A chopping board (<const>C</const>).</li>
            <li> An oven (<const>O</const>).</li>
          </ul>
      </div>
      <!-- END -->
      <!-- BEGIN level4 -->
        It also contains different food crates that dispense:
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Blueberries (<const>B</const>).</li>
          <li>Ice cream (<const>I</const>).</li>
          <li>Strawberries (<const>S</const>).</li>
          <li>Dough (<const>H</const>).</li>
        </ul>

        It also contains two extra appliances: 
          <ul style="padding-top:0; padding-bottom: 0;">
            <li>A chopping board (<const>C</const>).</li>
            <li> An oven (<const>O</const>).</li>
          </ul>
      <!-- END -->
      <br>
      <p>
        <b>The desserts</b>
      </p>
        The chefs can prepare two basic desserts:
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Blueberries (<const>BLUEBERRIES</const>).</li>
          <li>Ice cream (<const>ICE_CREAM</const>).</li>
        </ul>
      <!-- BEGIN level2 -->
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
        <p>
          The chefs can also prepare a classic dessert: chopped strawberries (<const>CHOPPED_STRAWBERRIES</const>). 
        </p>
        <p>
          Strawberries need to be cut at the chopping board before being added to a dish. <br/>
          <b>Chopping board:</b> <const>STRAWBERRIES</const> => (<const>CHOPPED_STRAWBERRIES</const>)
        </p>
      </div>
      <!-- END -->

      <!-- BEGIN level3 -->
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
          The chefs can also prepare two classic desserts: 
          <ul style="padding-top:0; padding-bottom: 0;">
            <li>Chopped strawberries (<const>CHOPPED_STRAWBERRIES</const>).</li>
            <li>Croissants (<const>CROISSANT</const>).</li>
          </ul>
      </div>
      <p>
        Strawberries need to be cut at the chopping board before being added to a dish. <br/>
        <b>Chopping board:</b> <const>STRAWBERRIES</const> => (<const>CHOPPED_STRAWBERRIES</const>)
      </p>
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
        <p>
          A ball of dough needs to be cooked into a croissant at the oven before being added to a dish. <br/>
          <b>Oven:</b> <const>DOUGH</const> => <const>CROISSANT</const>
        </p>
        <p>
          Cooking takes <const>10</const> turns, after which the food is <b>READY</b>. The food will remain <b>READY</b> for <const>10</const> more turns, after which it will be burned away and need to be restarted.
        </p>
      </div>
      <!-- END -->
      <!-- BEGIN level4 -->
        The chefs can also prepare two classic desserts: 
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Chopped strawberries (<const>CHOPPED_STRAWBERRIES</const>).</li>
          <li>Croissants (<const>CROISSANT</const>).</li>
        </ul>

      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
        <p>
          The chefs can also prepare one advanced dessert: blueberry tart (<const>TART</const>).
        </p>
      </div>
      
      <p>
        Strawberries need to be cut at the chopping board before being added to a dish. <br/>
        <b>Chopping board:</b> <const>STRAWBERRIES</const> => (<const>CHOPPED_STRAWBERRIES</const>)
      </p>
      <p>
        A ball of dough needs to be cooked into a croissant at the oven before being added to a dish. <br/>
        <b>Oven:</b> <const>DOUGH</const> => <const>CROISSANT</const>
      </p>

      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
        <p>
          To make a blueberry tart, a ball of dough needs to be chopped at the chopping board, then mixed with blueberries and then cooked into a blueberry tart in the oven before being added to a dish. <br/>
          <b>Chopping board:</b> <const>DOUGH</const> => <const>CHOPPED_DOUGH</const>
          <const>CHOPPED_DOUGH</const> + <const>BLUEBERRIES</const> => <const>RAW_TART</const>
          <b>Oven:</b> <const>RAW_TART</const> => <const>TART</const>
        </p>
      </div>
      <p>
        Cooking takes <const>10</const> turns, after which the food is <b>READY</b>. The food will remain <b>READY</b> for <const>10</const> more turns, after which it will be burned away and need to be restarted.
      </p>
      <!-- END -->
      <!-- BEGIN level5 -->
        The chefs can also prepare two classic desserts: 
        <ul style="padding-top:0; padding-bottom: 0;">
          <li>Chopped strawberries (<const>CHOPPED_STRAWBERRIES</const>).</li>
          <li>Croissants (<const>CROISSANT</const>).</li>
        </ul>
      <p>
        The chefs can also prepare one advanced dessert: blueberry tart (<const>TART</const>).
      </p>
      <p>
        Strawberries need to be cut at the chopping board before being added to a dish. <br/>
        <b>Chopping board:</b> <const>STRAWBERRIES</const> => (<const>CHOPPED_STRAWBERRIES</const>)
      </p>
      <p>
        A ball of dough needs to be cooked into a croissant at the oven before being added to a dish. <br/>
        <b>Oven:</b> <const>DOUGH</const> => <const>CROISSANT</const>
      </p>
        <p>
        To make a blueberry tart, a ball of dough needs to be chopped at the chopping board, then mixed with blueberries and then cooked into a blueberry tart in the oven before being added to a dish. <br/>
        <b>Chopping board:</b> <const>DOUGH</const> => <const>CHOPPED_DOUGH</const>
        <const>CHOPPED_DOUGH</const> + <const>BLUEBERRIES</const> => <const>RAW_TART</const>
        <b>Oven:</b> <const>RAW_TART</const> => <const>TART</const>
      </p>
      <p>
        Cooking takes <const>10</const> turns, after which the food is <b>READY</b>. The food will remain <b>READY</b> for <const>10</const> more turns, after which it will be burned away and need to be restarted.
      </p>
      <!-- END -->
      <!-- BEGIN level1 -->
      <img src="https://www.codingame.com/servlet/mfileservlet?id=25786355937691" style="padding: 20px; width: 100%;"></img>
      <!-- END -->
      <!-- BEGIN level2 -->
      <img src="https://www.codingame.com/servlet/mfileservlet?id=25786706826677" style="padding: 20px; width: 100%;"></img>
      <!-- END -->
      <!-- BEGIN level3 -->
      <img src="https://www.codingame.com/servlet/mfileservlet?id=25786714906811" style="padding: 20px; width: 100%;"></img>
      <!-- END -->
      <!-- BEGIN level4 -->
      <img src="https://www.codingame.com/servlet/mfileservlet?id=25786724844549" style="padding: 20px; width: 100%;"></img>
      <!-- END -->
      <br>
      <p>
        <b>The customers</b>
      </p>
      <p>
        At most 3 customers are waiting for their order. Each delivered order rewards both active chefs with points, but the longer the customer waits, the fewer points the chefs get.
      </p>
      <!-- BEGIN level1 -->
        Every customer requests exactly <const>ICE_CREAM</const> and <const>BLUEBERRIES</const>.      
      <!-- END -->
      <!-- BEGIN level2 -->
        Every customer requests between 2-3 items, among
        <ul style="padding-top:0; padding-bottom: 0;">
          <li><const>ICE_CREAM</const></li>
          <li><const>BLUEBERRIES</const></li>
          <li><const>CHOPPED_STRAWBERRIES</const></li>
        </ul>
      <!-- END -->
      <!-- BEGIN level3 -->      
        Every customer requests between 2-4 items, among
        <ul style="padding-top:0; padding-bottom: 0;">
          <li><const>ICE_CREAM</const></li>
          <li><const>BLUEBERRIES</const></li>
          <li><const>CHOPPED_STRAWBERRIES</const></li>
          <li><const>CROISSANT</const></li>
        </ul>      
      <!-- END -->
      <!-- BEGIN level4 -->      
        Every customer requests between 2-4 items, among
        <ul style="padding-top:0; padding-bottom: 0;">
          <li> <const>ICE_CREAM</const></li>
          <li><const>BLUEBERRIES</const></li>
          <li><const>CHOPPED_STRAWBERRIES</const></li>
          <li><const>CROISSANT</const></li>
          <li><const>TART</const></li>
        </ul>
      <!-- END -->
      <!-- BEGIN level2 level3 level4 -->
      (no duplicates).
      <!-- END -->

      <!-- BEGIN level1 -->
      <p>
        A customer's order should be served on a dish (<const>DISH</const>).
      </p>
      <!-- END -->
      <!-- BEGIN level2 -->
      <div style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
        <p>
          A customer's order should be served on a dish (<const>DISH</const>). A dish can only contain (finished) desserts.
        </p>
      </div>
      <!-- END -->
      <!-- BEGIN level3 level4 -->
      <p>
        A customer's order should be served on a dish (<const>DISH</const>). A dish can only contain (finished) desserts.
      </p>
      <!-- END -->
      <p>
        There are at maximum <const>3</const> dishes in play. As soon as an order is sent through the window, a new dish appears in the dishwasher.
      </p>
      <br>
      <p>
        <b>Actions</b>
      </p>
      
      <p>
        <action>MOVE x y</action>
      </p>
      <p>
        Use this command to move towards a different cell. The chefs move horizontally and vertically, of <const>4</const> cells at most. They can't occupy the same cell or pass through each other.
      </p>
      <br>
      <p>
        <action>USE x y</action>
      </p>
      <p>
        Use this command to interact with the cell (x,y). If the chef is adjacent to the cell when using the <action>USE</action> command, the action is successful. Otherwise, the chef will move closer to that cell. The <action>USE</action> command works diagonally (8-adjacency).
      </p>
      <p>
        Depending on the cell and what the chef is holding, the <action>USE</action> will have different effects. The main effects are summarized below:
      </p>
      <ul style="padding-top:0; padding-bottom: 0;">
          <li>
            The <action>USE</action> action on an equipment will make you use that equipment.
          </li>
          <li>
            The <action>USE</action> action on a table with an item (food or dish) while holding nothing will make you pick up that item.
          </li>
          <li>
            The <action>USE</action> action on a table with a finished dessert while holding a dish will make you add that dessert to the dish.
          </li>
          <!-- BEGIN level4 -->
          <li style="color: #7cc576;
        background-color: rgba(124, 197, 118,.1);
        padding: 2px;">
            The <action>USE</action> action on a table with food while holding food will make you attempt to fuse both. </br>
            (works only if food is: <const>CHOPPED_DOUGH</const> and <const>BLUEBERRIES</const>)
          </li>
          <!-- BEGIN level5 -->
          <li>
            The <action>USE</action> action on a table with food while holding food will make you attempt to fuse both. </br>
            (works only if food is: <const>CHOPPED_DOUGH</const> and <const>BLUEBERRIES</const>)
          </li>
          <!-- END -->
        </ul>
          <br>
      <p>
        <action>WAIT</action>
      </p>
      <p>
        Use this command to do nothing.
      </p>
      <br>
      <p>
        To display a message in a viewer, append a semicolon followed by your message to the output. <br>
        Ex: <action>USE 0 0; my message</action>
      </p>
    </div>
  </div>

  <!-- Victory conditions -->
  <div class="statement-victory-conditions">
    <div class="icon victory"></div>
    <div class="blk">
      <div class="title">Victory Conditions</div>
      <div class="text">
        <ul style="padding-top: 0;padding-bottom: 0;">
          <li>
            You earn more points than your opponents after the three rounds.
          </li>
        </ul>
      </div>
    </div>
  </div>

  <!-- Lose conditions -->
  <div class="statement-lose-conditions">
    <div class="icon lose"></div>
    <div class="blk">
      <div class="title">Loss Conditions</div>
      <div class="text">
        <ul style="padding-top: 0;padding-bottom: 0;">
          <li>
            Your program times out.
          </li>
          <li>
            Your program provides invalid output.
          </li>
        </ul>
      </div>
    </div>
  </div>
  <br>

  <!-- EXPERT RULES -->
  <div class="statement-section statement-expertrules">
    <h2>
      <span class="icon icon-expertrules">&nbsp;</span>
      <span>Advanced Details</span>
    </h2>
    <div class="statement-expert-rules-content">
      <p>
        You can see the game's source code here: <a target="_blank" href="https://github.com/csj/code-a-la-mode">https://github.com/csj/code-a-la-mode</a>.
      </p>
        <ul style="padding-top: 0;padding-bottom: 0;">
          <li>
            The chefs cannot exchange any food or dish with each other. They need to put it down on a table for the other to pick it up.
          </li>
          <li>
            The chefs cannot put food or dishes on the floor.
          </li>
          <li>
            The chefs cannot pick up a dish if they're already carrying one.
          </li>
          <li>
            As soon as food is put on a dish, it cannot be removed from it. To empty a dish, <action>USE</action> the dishwasher while holding it.
          </li>
          <li>
            A dish cannot contain more than <const>4</const> desserts.
          </li>
          <li>
            All possible cases of the <action>USE</action> are listed <a target="_blank" href="https://github.com/csj/code-a-la-mode/blob/master/USE.md">here</a>.
          </li>
          <li>
            For every turn a customer waits for an order, the reward is decreased by <const>1</const>.
          </li>
        </ul>
    </div>
  </div>
  <!-- PROTOCOL -->
  <div class="statement-section statement-protocol">
    <h2>
      <span class="icon icon-protocol">&nbsp;</span>
      <span>Game Input</span>
    </h2>

    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Input for the first turn</div>
      <div class="text">
        <span class="statement-lineno">First line</span>: an integer <var>numAllCustomers</var> for the total number of customers (same list of customers for each round).<br>
        <span class="statement-lineno">Next <var>numAllCustomers</var> lines</span>:
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            A string <var>customerItem</var> for the customer's order <br/>
            Ex: <const>DISH-BLUEBERRIES-ICE_CREAM</const>
          </li>
          <li>
            An integer <var>customerAward</var> for the number of points awarded if the customer's order is delivered
          </li>
        </ul>
        <span class="statement-lineno">Next <const>7</const> lines</span>: A string <var>kitchenLine</var> of size 11 representing a part of the kitchen. <br/>
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            <const>.</const>: walkable cell
          </li>
          <li>
            <const>0</const>: first player spawn location (also walkable)
          </li>
          <li>
            <const>1</const>: second player spawn location (also walkable)
          </li>
          <li>
            <const>D</const>: the dishwasher
          </li>
          <li>
            <const>W</const>: the window
          </li>
          <li>
            <const>B</const>: the blueberry crate
          </li>
          <li>
            <const>I</const>: the ice cream crate
          </li>
          <!-- BEGIN level2 level3 level4 level5 -->
          <li>
            <const>S</const>: the strawberry crate
          </li>
          <li>
            <const>C</const>: the chopping board
          </li>
          <!-- END -->
          <!-- BEGIN level3 level4 level5 -->
          <li>
            <const>H</const>: the dough crate
          </li>
          <li>
            <const>O</const>: the oven
          </li>
          <!-- END -->
        </ul>
      </div>
    </div>
    <div class="blk">
      <div class="title">Input for one game turn</div>
      <div class="text">
        <span class="statement-lineno">First line</span>: An integer <var>turnsRemaining</var> for the number of turns remaining before the end of the current round. <br/>
        <span class="statement-lineno">Next <const>3</const> lines</span>: 
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            Two integers <var>playerX</var> and <var>playerY</var> for the player's chef position
          </li>
          <li>
            A string <var>playerItem</var> for what the player's chef is carrying <br/>
            Ex: <const>DISH-BLUEBERRIES-ICE_CREAM</const><br/>
            If no item is being carried: <const>NONE</const>
          </li>
        </ul>
        <span class="statement-lineno">Next <const>3</const> lines</span>: 
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            Two integers <var>partnerX</var> and <var>partnerY</var> for the other player's chef position
          </li>
          <li>
            A string <var>partnerItem</var> for what the other player's chef is carrying
          </li>
        </ul>
        <span class="statement-lineno">Next line</span>: An integer <var>numTablesWithItems</var> for the number of non-empty tables <br/>
        <span class="statement-lineno">Next <var>numTablesWithItems</var> lines</span>:
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            Two integers <var>tableX</var> and <var>tableY</var> for the table's position
          </li>
          <li>
            A string <var>item</var> for what's on the table. <br/>
            Ex: <const>DISH-BLUEBERRIES-ICE_CREAM</const>
          </li>
        </ul>
        <!-- BEGIN level1 level2 -->
        <span class="statement-lineno">Next line</span>: to ignore in this league <br/>
        <!-- END -->
        <!-- BEGIN level3 level4 level5 -->
        <span class="statement-lineno">Next line</span>: A string <var>ovenContents</var> for what's in the oven and an integer <var>ovenTimer</var> for the number of turns the food will stay in the oven before being cooked or burned. <br/>
        <!-- END -->
        <span class="statement-lineno">First line</span>: an integer <var>numCustomers</var> for the current number of customers waiting for their order. <br/>
        <span class="statement-lineno">Next <var>numCustomers</var> lines</span>:
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
            A string <var>customerItem</var> for the customer's order <br/>
            Ex: <const>DISH-BLUEBERRIES-ICE_CREAM</const>
          </li>
          <li>
            An integer <var>customerAward</var> for the number of points awarded if the customer's order is delivered
          </li>
        </ul>
      </div>
    </div>

    <!-- Protocol block -->
    <div class="blk">
      <div class="title" style="padding-bottom: 0;">Output for a turn</div>
      <div class="text">
        <ul style="margin-top: 0;margin-bottom: 0;padding-bottom: 0;">
          <li>
            <action>MOVE x y</action> to move to the cell (x,y).
          </li>
          <li>
            <action>USE x y</action> to interact with the cell (x,y).
          </li>
          <li>
            <action>WAIT</action> to do nothing.
          </li>
        </ul>
      </div>
    </div>

    <!-- Constraints block -->
    <div class="blk">
      <div class="title">Constraints</div>
      <div class="text">
        Response time for the first turn ≤ <const>1</const>s<br>
        Response time per turn ≤ <const>50</const>ms<br>
      </div>
    </div>
  </div>

<!-- BEGIN level1 level2 level3 -->
  <div style="color: #7cc576;
      background-color: rgba(124, 197, 118,.1);
      padding: 20px;
      margin-top: 10px;
      text-align: left;">
    <div style="text-align: center; margin-bottom: 6px">
      <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
    </div>
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      What is in store in the higher leagues?
    </p>
      The extra rules available in higher leagues are:
      <ul style="margin-top: 0;padding-bottom: 0;" class="statement-next-rules">
        <!-- BEGIN level1 -->
        <li>In Wood 2, chefs can cut strawberries at the chopping board.</li>
        <!-- END -->
        <!-- BEGIN level1 level2 -->
        <li>In Wood 1, chefs can cook a dough into croissants.</li>
        <!-- END -->
        <!-- BEGIN level1 level2 level3 -->
        <li>In Bronze, chefs can prepare blueberry tarts.</li>
        <!-- END -->
      </ul>
  </div>
  <!-- END -->