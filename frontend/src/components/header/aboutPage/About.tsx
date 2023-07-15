import React from 'react';
import './About.css';

interface AboutProps {

}

const heightVH: number = 70;
const buttonHeightVH: number = 6;

export default function About({  }: AboutProps): JSX.Element {
    const [showAbout, setShowAbout] = React.useState(false);

    return (
        <div className="About"
            style={{
                top: showAbout ? 0 + "vh" : (-heightVH) + "vh",
                height: heightVH + "vh"
            }}
        >
            <div className="content">
                <h2>What is this?</h2>
                <p>
                    This is a statistics tool for the game Teamfight Tactics.
                    It is no "AI" solution, but it can very effectively show you
                    what kind of units, traits, and items you should be looking
                    for in your games based on evaluations of the current meta,
                    by viewing any game of TFT very broadly: 
                    It records how many times a unit, trait or item is used in combination,
                    and what placement this resulted in. There is a lot of other ways
                    to use this than just that however. 
                    These combinations are recorded in models and sorted, filtered and presented here, 
                    for your pleasure. :D
                </p>
                <h2>How do I use this?</h2>
                <p>
                    You can see any models created in the "Browse" tab. Or, alternatively,
                    you can create your own model trained from your games and get insights from these.
                    When viewing a model, the view is always based on a selected point and shows how
                    often that point appears in conjuction with other points.
                    I.e. it can show you that, of some amount of games, there's a strong correlation
                    between the Noxian trait and 8th placement. 
                    Or that 1st place usually happens when the player has any amount of Guinsoo's aswell.
                    Take from that what you will. 
                </p>
                <h2>Why is this?</h2>
                <p>
                    Because I thought it'd be fun, and interesting to make. 
                    I do not play TFT competitively, but I do enjoy trying different things.
                    Took me a weekend to make, and I'm pretty happy with the result.
                </p>
                <h2>Who is this?</h2>
                <p>
                    I'm an engeenering student specializing in web services currently studying at university and very fond of games. 
                </p>
            </div>

            <button className="about-button"
                onClick={() => setShowAbout(!showAbout)}
                style={{
                    height: buttonHeightVH + "vh",
                    bottom: (-buttonHeightVH) + "vh"
                }}
            >
                {
                    showAbout ?
                        "^"
                        :
                        "What is this?"
                }
            </button>
        </div>
    )
}