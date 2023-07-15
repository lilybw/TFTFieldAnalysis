import React from 'react';
import './About.css';

interface AboutProps {

}

const topRep: number = 31;
const heightRem: number = 40;
const heightButtonRem: number = 4;

export default function About({  }: AboutProps): JSX.Element {
    const [showAbout, setShowAbout] = React.useState(false);

    return (
        <div className="About"
            style={{
                top: showAbout ? 0 + "rem" : (-heightRem) + "rem",
                height: heightRem + "rem"
            }}
        >
            <div className="content">
                <h2>What is this?</h2>
                <p>
                    Lorem alsdjlaksj alskdj lasj dlasj dlk aslkdjalskdj
                    laksjdl aslkdj laksjdlkajsdlkja slkdja lsdj lkasjd
                    lkajsld kjaslkd jlaskj dlkajs dlkjas ldkjaslk djlaskjd
                    laksjd lkajsdl kjasldkj alskd lkasjd lkajs ldkjalskdj
                    laksj dlkasj dlkajsd lkjas
                </p>
                <h2>Why is this?</h2>
                <p>
                    Lorem alsdjlaksj alskdj lasj dlasj dlk aslkdjalskdj
                    laksjdl aslkdj laksjdlkajsdlkja slkdja lsdj lkasjd
                    lkajsld kjaslkd jlaskj dlkajs dlkjas ldkjaslk djlaskjd
                    laksjd lkajsdl kjasldkj alskd lkasjd lkajs ldkjalskdj
                    laksj dlkasj dlkajsd lkjas
                </p>
                <h2>Who is this?</h2>
                <p>
                    Lorem alsdjlaksj alskdj lasj dlasj dlk aslkdjalskdj
                    laksjdl aslkdj laksjdlkajsdlkja slkdja lsdj lkasjd
                    lkajsld kjaslkd jlaskj dlkajs dlkjas ldkjaslk djlaskjd
                    laksjd lkajsdl kjasldkj alskd lkasjd lkajs ldkjalskdj
                    laksj dlkasj dlkajsd lkjas
                </p>
            </div>

            <button className="about-button"
                onClick={() => setShowAbout(!showAbout)}
                style={{
                    height: heightButtonRem + "rem",
                    bottom: (-heightButtonRem) + "rem"
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