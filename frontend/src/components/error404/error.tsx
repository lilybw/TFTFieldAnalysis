import { useRouteError } from "react-router-dom";

import './error.css';

interface ErrorPageProps{
  message?: string
}

export default function ErrorPage({message}: ErrorPageProps) {
  const error: any = useRouteError();

  return (
    <div className="Error404">
      <h1>Oops!</h1>
      {
        message ? 
        <p>{message}</p>
        :
        <p>Sorry, an unexpected error has occurred.</p>
      
      }
      <p>
        <i>{error.statusText || error.message}</i>
      </p>
    </div>
  );
}