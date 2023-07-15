import React from 'react';
import './Footer.css';

interface FooterProps {
}

export default function Footer({ }: FooterProps): JSX.Element {
    return (
        <div className="Footer">
            <p>©Gustav B. Wanscher 2023</p>
        </div>
    )
}