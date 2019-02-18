<!-- LEAGUES level1 level2 level3 level4 -->
<div class="statement-body">
  <!-- BEGIN level1 level2 level3 -->
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
      Welcome to the Wood1 league!
      <!-- END -->
      <!-- BEGIN level3 -->
      Welcome to the Bronze league!
      <!-- END -->
    </p>
    <span class="statement-league-alert-content">
      <!-- BEGIN level1 -->
      Wood leagues should be considered as a tutorial which lets players discover the different rules of the game. <br>
      In Bronze league, all rules will be unlocked and the real challenge will begin.
      <!-- END -->
      <!-- BEGIN level2 -->
      In Wood 1, .
      <!-- END -->
      <!-- BEGIN level3 -->
      In Bronze, .
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
      Control a chef and prepare food for customers as quick as possible, and earn more points than other players.
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
        This is a three-player game played on a grid of size w*h. A match is played in 3 rounds, each with 2 of the players.
      </p>
      <p>
        Round 1: player A vs player B <br>
        Round 2: player A vs player C <br>
        Round 3: player B vs player C <br>
      </p>
      <p>
        Each player will thus play two rounds in each match. A player total points is the sum of both rounds' points.
      </p>
      <p>
        Each round lasts for <const>150</const> turns and plays the same kitchen and customers conditions.
      </p>
      <p>
        <b>A round</b>
      </p>
      <p>
        Both players control a chef who moves around the kitchen and prepare food for customers. Both players play collaboratively, and one after the other.
      </p>
      <p>
        <b>The kitchen</b>
      </p>
      <p>
        The kitchen contains: floor cells (on which the chefs move), a bin, empty tables, a plate disposer and a customer window.
      </p>
      <p>
        It also contains different food crates that dispense either blueberries, ice cream, strawberries or dough.
      </p>
      <p>
        It also contains different appliances: a chopping board and an oven.
      </p>
      <p>
        <b>The customers</b>
      </p>
      <p>
        At most 3 customers are waiting for their order. A customer's order can contain 2 to 4 desserts. Each validated order rewards the team of chefs with points, but the longer the customer waits, the less points they get.
      </p>
      <p>
        A customer's order should be served on a plate: <const>0 - plate</const>.
      </p>
      <p>
        There are maximum 3 plates in play. As soon as an order is validated, a new plate is made available in the disposer.
      </p>
      <p>
        <b>The desserts</b>
      </p>
      <p>
        The chefs can prepare two basic desserts: <const>1 - blueberries</const>, <const>2 - ice cream</const>.
      </p>
      <p>
        The chefs can also prepare two intermediate desserts: <const>3 - strawberries</const>, and <const>4 - croissants</const>. Strawberries need to be cut at the chopping board before being dressed. A dough needs to be cooked into croissants at the oven before being dressed.
      </p>
      <p>
        The chefs can also prepare one advanced dessert: <const>5 - blueberry pie</const>. A dough need to be chopped at the chopping board, then mixed with blueberries and cooked into a blueberry pie in the oven before being dressed. Cooking takes <const>n?</const> turns.
      </p>
      <p>
        If food is not taken from the oven after <const>m?</const> turns, the food gets burned and must be thrown into the bin.
      </p>
      <p>
        <b>Actions</b>
      </p>
      <p>
        To move to a different cell, use the command <action>MOVE x y</action>. A chef moves horizontally and vertically, of <const>a?</const> cells at most.
      </p>
      <p>
        To pick up an ingredient from a crate or a table, use the command <action>USE x y</action>. If the chef is adjacent to the corresponding cell, the ingredient is picked up; else, the chef will move closer to that cell.
      </p>
      <p>
        To mix different types of food, a chef must use a plate. Hence, a chef can carry at most 1 type of food unless carrying a plate.
      </p>
      <p>
        The <action>USE x y</action> command also allows to put what the chef is carrying on a table (or drop it in the bin). The pick up action has precedence over the put down action.
      </p>
      <p>
        To chop food, use the `USE` command. The action will succeed only if the chef is carrying strawberries or a dough, with or without a plate.
      </p>
      <p>
        To cook the dough into croissants, use the `USE` command. The action will succeed only if the chef is carrying a dough, with or without a plate.
      </p>
      <p>
        To cook the dough into croissants or berry tart, use the `USE` command. The action will succeed only if <br>
        - the chef is carrying a dough and/or a plate in which case croissants will be cooked. <br>
        - the chef is carrying a chopped dough and blueberries in a plate in which case a berry tart will be cooked.
      </p>
      <p>
        To do nothing, use <action>WAIT</action>.
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
            You earn more points than your opponents after three rounds.
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
            Your program provides invalid output for the active turn type.
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
        You can see the game's source code here: <a href=""></a>.
      </p>
      <p>
        <ul style="padding-top: 0;padding-bottom: 0;">
          <li>
            The chefs cannot exchange any food or plate between each other. They need to put it down on a table for the other to pick it up.
          </li>
          <li>
            The chefs cannot put food on the floor.
          </li>
          <li>
            The chefs cannot pick up plates if they're already carrying a plate.
          </li>
          <li>
            As soon as food is put on a plate, it cannot be removed from it. To empty a plate, USE the bin.
          </li>
          <li>
            For every turn a customer waits for an order, the reward is decreased by 1.
          </li>
          <li>
            A plate cannot contain more than 4 desserts.
          </li>
        </ul>
      </p>
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
      <div class="title">Input for one game turn</div>
      <div class="text">
        <span class="statement-lineno">First line</span>: 
        <span class="statement-lineno">Next <const>7</const> lines</span>: 
        <span class="statement-lineno">Next <const>2</const> lines</span>: 
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
          </li>
        </ul>
        <span class="statement-lineno">Next line</span>: 
        <span class="statement-lineno">Next 
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
          </li>
        </ul>
        <span class="statement-lineno">Next line</span>: 
        <span class="statement-lineno">Next 
        <ul style="margin-top: 0;padding-bottom: 0;">
          <li>
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
            <action>USE x y</action> to pick up/drop a plate, use a crate or an appliance.
          </li>
          <li>
            <action>WAIT</action> to skip moving this turn.
          </li>
        </ul>
      </div>
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

  <!-- BEGIN level1 level2 level3-->
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
    <p>
      The extra rules available in higher leagues are:
      <ul style="margin-top: 0;padding-bottom: 0;" class="statement-next-rules">
        <!-- BEGIN level1 -->
        <li>In Wood 1, </li>
        <!-- END -->
        <!-- BEGIN level1 level2 -->
        <li>In Bronze, </li>
        <!-- END -->
      </ul>
    </p>
  </div>
  <!-- END -->

</div>
